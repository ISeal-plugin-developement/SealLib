package dev.iseal.sealLib.Updater;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import dev.iseal.sealLib.Metrics.ConnectionManager;
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

    private String id = "8ghxB9YU";
    private String currentVersion;
    private String notifyPermission = "sealLib.notify";
    private int checkInterval = 3600;
    private Consumer<Exception> onFail = s -> {};
    private BiConsumer<String, String> onNewVersion = (s, s2) -> {};
    private JavaPlugin plugin;
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

        if (isDebug()) l.info("[SealLib] UpdateChecker initialized with ID: " + id + ", current version: " + currentVersion);
    }

    /**
     * Checks for new versions of the plugin
     */
    public void check(String sender) {
        if (isDebug()) l.info("[SealLib] Starting update check for sender: " + sender);

        if (isIDValid == null) {
            if (isDebug()) l.info("[SealLib] Checking if ID is valid");
            String[] data = ConnectionManager.getInstance().sendDataToModrinth("project/"+id+"/check");
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
        String[] data = ConnectionManager.getInstance().sendDataToModrinth("project/"+id+"/version");
        if (!data[1].equals("200")) {
            onFail.accept(new IOException("Failed to get available versions"));
            return;
        }
        if (isDebug()) l.info("[SealLib] Got versions");

        JsonArray versionsArray = JsonParser.parseString(data[0]).getAsJsonArray();
        if (versionsArray.size() == 0) {
            onFail.accept(new IOException("No versions found"));
            return;
        }
        String version = versionsArray.get(0).getAsJsonObject().get("version_number").getAsString();
        if (isDebug()) l.info("[SealLib] Got version: "+version + " (latest) and "+currentVersion + " (current)");

        char[] currentVersionChars = currentVersion.toCharArray();
        char[] versionChars = version.toCharArray();

        if (currentVersionChars.length != versionChars.length) {
            isOutOfDate = true;
            onNewVersion.accept(version, sender);
            if (isDebug()) l.info("[SealLib] Current version length differs from latest version length. Marking as out of date.");
            return;
        }

        int[] currentVersionInts = new int[currentVersionChars.length];
        int[] versionInts = new int[versionChars.length];

        int index = 0;

        for (int i = 0; i < currentVersionChars.length; i++) {
            char currentChar = currentVersionChars[i];
            char versionChar = versionChars[i];
            if (Character.isDigit(currentChar)) index++;
            if (Character.isDigit(currentChar)) currentVersionInts[index] = Character.getNumericValue(currentChar);
            if (Character.isDigit(versionChar)) versionInts[index] = Character.getNumericValue(versionChar);
        }

        for (int i = 0; i < currentVersionInts.length; i++) {
            if (currentVersionInts[i] == versionInts[i]) continue;
            if (currentVersionInts[i] > versionInts[i]) return;

            if (currentVersionInts[i] < versionInts[i]) {
                isOutOfDate = true;
                newVersion = version;
                onNewVersion.accept(version, sender);
                if (isDebug()) l.info("[SealLib] New version found: " + version + ". Marking as out of date.");
                l.info("[SealLib] A new version of "+plugin.getDescription().getName()+" is available! ("+currentVersion+" -> "+newVersion+")");
                return;
            }
        }

        if (isDebug()) l.info("[SealLib] No new version found. Current version is up to date.");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission(notifyPermission) && isOutOfDate) {
            Player player = event.getPlayer();
            player.sendMessage(ChatColor.DARK_GREEN+"[SealLib] A new version of "+plugin.getDescription().getName()+" is available! ("+currentVersion+" -> "+newVersion+")");
        }
    }
}
