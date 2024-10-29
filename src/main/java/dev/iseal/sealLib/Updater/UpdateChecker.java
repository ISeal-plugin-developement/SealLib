package dev.iseal.sealLib.Updater;

import com.google.gson.Gson;
import dev.iseal.sealLib.Metrics.ConnectionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static dev.iseal.sealLib.SealLib.isDebug;

public class UpdateChecker implements Listener {

    private String id = "8ghxB9YU";
    private String currentVersion;
    private String notifyPermission = "sealLib.notify";
    private int checkInterval = 3600;
    private Consumer<Exception> onFail = s -> {};
    private BiConsumer<String, String> onNewVersion = (s, s2) -> {};

    private Boolean isIDValid = null;
    private boolean isOutOfDate = false;

    /*
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
    }

    /**
     * Checks for new versions of the plugin
     */
    public void check(String sender) {
        if (isDebug() || currentVersion.contains("DEV") || currentVersion.contains("SNAPSHOT")) {
            //ignore the check
            return;
        }

        Gson gson = new Gson();
        if (!isIDValid) {
            onFail.accept(new IOException("Invalid ID"));
            return;
        }

        if (isIDValid == null) {
            String[] data = ConnectionManager.getInstance().sendDataToModrinth("project/"+id+"/check");
            int rCode = Integer.parseInt(data[1]);
            if (rCode != 200) {
                isIDValid = false;
                onFail.accept(new IOException("Invalid ID"));
                return;
            }
            isIDValid = true;
        }
        String[] data = ConnectionManager.getInstance().sendDataToModrinth("project/"+id+"/version");
        if (!data[1].equals("200")) {
            onFail.accept(new IOException("Failed to get available versions"));
            return;
        }
        String[] versions = (String[]) ((HashMap<String, Object>) gson.fromJson(data[0], Map.class)).get("versions");
        if (versions.length == 0) {
            onFail.accept(new IOException("No versions found"));
            return;
        }
        String latestVersionID = versions[versions.length-1];
        data = ConnectionManager.getInstance().sendDataToModrinth("version/"+latestVersionID);
        String latestVerNumber = (String) ((HashMap<String, Object>) gson.fromJson(data[0], Map.class)).get("version_number");
        if (!latestVerNumber.equals(currentVersion)) {
            isOutOfDate = true;
            onNewVersion.accept(latestVerNumber, sender);
            return;
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission(notifyPermission) && isOutOfDate) {
            Player player = event.getPlayer();
            player.sendMessage();
        }
    }
}
