package dev.iseal.sealLib.Updater;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import dev.iseal.sealLib.Metrics.ConnectionManager;
import dev.iseal.sealLib.SealLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static dev.iseal.sealLib.SealLib.isDebug;

public class UpdateChecker implements Listener {

    private final String id;
    private final String currentVersion;
    private final String notifyPermission;
    private final int checkInterval;
    private final Consumer<Exception> onFail;
    private final BiConsumer<String, String> onNewVersion;
    private final JavaPlugin plugin;
    private String newVersion;
    private final Logger l = Bukkit.getLogger();

    private Boolean isIDValid = null;
    private boolean isOutOfDate = false;

    /*
     * Create an update checker for a plugin
     *
     * @param id The id of the plugin on modrinth
     * @param currentVersion The current version of the plugin
     * @param notifyPermission The permission to notify the player n join
     * @param checkInterval The interval to check for updates in ticks
     * @param onFail The consumer to run when the update check fails
     * @param onNewVersion The consumer to run when a new version is found, accepts the new version and the player that sent the command, in order
     *
     *
     */
    public UpdateChecker(String id, JavaPlugin plugin, String notifyPermission, int checkInterval, Consumer<Exception> onFail, BiConsumer<String, String> onNewVersion) {
        // Check for updates
        // default is sealapi id
        this.id = id;
        currentVersion = plugin.getDescription().getVersion();
        this.plugin = plugin;
        this.notifyPermission = notifyPermission;
        this.checkInterval = checkInterval;
        this.onFail = onFail;
        this.onNewVersion = onNewVersion;

        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        new BukkitRunnable() {
            @Override
            public void run() {
                check("CONSOLE");
            }
        }.runTaskTimerAsynchronously(plugin, 0, checkInterval);

        if (isDebug())
            l.info("[SealLib] UpdateChecker initialized with ID: " + id + ", current version: " + currentVersion);
    }

    /**
     * Checks for new versions of the plugin
     */
    public void check(String sender) {
        if (isDebug()) l.info("[SealLib] Starting update check for sender: " + sender);

        if (isIDValid == null) {
            if (isDebug()) l.info("[SealLib] Checking if ID is valid");
            String[] data = ConnectionManager.getInstance().sendDataToModrinth("project/" + id + "/check");
            int rCode = Integer.parseInt(data[1]);
            if (rCode != 200) {
                if (isDebug()) l.info("[SealLib] ID is invalid");
                isIDValid = false;
                onFail.accept(new IOException("Invalid ID"));
                return;
            }
            isIDValid = true;
            if (isDebug()) l.info("[SealLib] ID is valid");
        }

        if (!isIDValid) {
            onFail.accept(new IOException("Invalid ID"));
            return;
        }

        if (isDebug()) l.info("[SealLib] Fetching available versions for project ID: " + id);
        String[] data = ConnectionManager.getInstance().sendDataToModrinth("project/" + id + "/version");
        if (!data[1].equals("200")) {
            onFail.accept(new IOException("Failed to get available versions"));
            return;
        }
        if (isDebug()) l.info("[SealLib] Got versions");

        JsonArray versionsArray = JsonParser.parseString(data[0]).getAsJsonArray();
        if (versionsArray.isEmpty()) {
            onFail.accept(new IOException("No versions found"));
            return;
        }
        String version = versionsArray.get(0).getAsJsonObject().get("version_number").getAsString();
        if (isDebug()) l.info("[SealLib] Got version: " + version + " (latest) and " + currentVersion + " (current)");

        // Check alpha/beta status
        boolean isCurrentAlpha = currentVersion.contains("ALPHA");
        boolean isVersionAlpha = version.contains("ALPHA");
        boolean isCurrentBeta = currentVersion.contains("BETA");
        boolean isVersionBeta = version.contains("BETA");

        // Skip if latest version is alpha/beta and not allowed in config
        if ((isVersionAlpha && !SealLib.isAllowAlpha()) ||
                (isVersionBeta && !SealLib.isAllowBeta())) {
            if (isDebug()) l.info("[SealLib] Skipping update check for " + version +
                    " because alpha/beta versions are not allowed in config.");
            return;
        }

        // Remove initial 'v' or 'V' if present
        String sanitizedCurrentVersion = currentVersion;
        String sanitizedVersion = version;

        if (!sanitizedCurrentVersion.isEmpty() && (sanitizedCurrentVersion.charAt(0) == 'v' || sanitizedCurrentVersion.charAt(0) == 'V')) {
            sanitizedCurrentVersion = sanitizedCurrentVersion.substring(1);
        }
        if (!sanitizedVersion.isEmpty() && (sanitizedVersion.charAt(0) == 'v' || sanitizedVersion.charAt(0) == 'V')) {
            sanitizedVersion = sanitizedVersion.substring(1);
        }

        // Split version into base and pre-release parts (handle max.major.minor.patch-BETAbetaVersion)
        String[] currentVersionParts = sanitizedCurrentVersion.split("-", 2);
        String[] versionParts = sanitizedVersion.split("-", 2);

        String currentBaseVersion = currentVersionParts[0];
        String versionBaseVersion = versionParts[0];

        // Compare base versions (max.major.minor.patch)
        String[] currentSegments = currentBaseVersion.split("\\.");
        String[] versionSegments = versionBaseVersion.split("\\.");

        // Compare version segments (max, major, minor, patch)
        int maxSegments = Math.max(currentSegments.length, versionSegments.length);
        for (int i = 0; i < maxSegments; i++) {
            int currentNum = (i < currentSegments.length) ? parseInt(currentSegments[i]) : 0;
            int versionNum = (i < versionSegments.length) ? parseInt(versionSegments[i]) : 0;

            if (currentNum < versionNum) {
                // Remote version is newer
                isOutOfDate = true;
                newVersion = version;
                onNewVersion.accept(version, sender);
                if (isDebug()) l.info("[SealLib] New version found: " + version + ". Marking as out of date.");
                l.info("[SealLib] A new version of " + plugin.getDescription().getName() + " is available! (" + currentVersion + " -> " + newVersion + ")");
                return;
            } else if (currentNum > versionNum) {
                // Current version is newer
                return;
            }
        }

        // Base versions are equal, check pre-release status
        boolean isCurrentPrerelease = currentVersionParts.length > 1;
        boolean isVersionPrerelease = versionParts.length > 1;

        // Full release is newer than any pre-release
        if (!isCurrentPrerelease && isVersionPrerelease) {
            return; // Current is newer (stable vs pre-release)
        }

        if (isCurrentPrerelease && !isVersionPrerelease) {
            // Remote is full release, current is pre-release
            isOutOfDate = true;
            newVersion = version;
            onNewVersion.accept(version, sender);
            if (isDebug()) l.info("[SealLib] New stable version found: " + version + ". Marking as out of date.");
            l.info("[SealLib] A new version of " + plugin.getDescription().getName() + " is available! (" + currentVersion + " -> " + newVersion + ")");
            return;
        }

        if (isCurrentPrerelease && isVersionPrerelease) {
            // Both are pre-releases, compare type (BETA > ALPHA)
            if (isCurrentAlpha && isVersionBeta) {
                isOutOfDate = true;
                newVersion = version;
                onNewVersion.accept(version, sender);
                if (isDebug()) l.info("[SealLib] New beta version found: " + version + ". Marking as out of date.");
                l.info("[SealLib] A new version of " + plugin.getDescription().getName() + " is available! (" + currentVersion + " -> " + newVersion + ")");
                return;
            }

            if (isCurrentBeta && isVersionAlpha) {
                return; // Current is newer
            }

            // Same type (both ALPHA or both BETA), compare numbers
            if ((isCurrentAlpha && isVersionAlpha) || (isCurrentBeta && isVersionBeta)) {
                int currentPreNumber = extractPreReleaseNumber(currentVersionParts[1]);
                int versionPreNumber = extractPreReleaseNumber(versionParts[1]);

                if (currentPreNumber < versionPreNumber) {
                    isOutOfDate = true;
                    newVersion = version;
                    onNewVersion.accept(version, sender);
                    if (isDebug()) l.info("[SealLib] New version found: " + version + ". Marking as out of date.");
                    l.info("[SealLib] A new version of " + plugin.getDescription().getName() + " is available! (" + currentVersion + " -> " + newVersion + ")");
                    return;
                }
            }
        }

        if (isDebug()) l.info("[SealLib] No new version found. Current version is up to date.");
    }

    // Helper methods
    private int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int extractPreReleaseNumber(String preRelease) {
        try {
            if (preRelease.contains("ALPHA")) {
                return Integer.parseInt(preRelease.replace("ALPHA", "").trim());
            } else if (preRelease.contains("BETA")) {
                return Integer.parseInt(preRelease.replace("BETA", "").trim());
            }
        } catch (NumberFormatException e) {
            return 0;
        }
        return 0;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission(notifyPermission) && isOutOfDate) {
            Player player = event.getPlayer();
            player.sendMessage(ChatColor.DARK_GREEN + "[SealLib] A new version of " + plugin.getDescription().getName() + " is available! (" + currentVersion + " -> " + newVersion + ")");
        }
    }
}