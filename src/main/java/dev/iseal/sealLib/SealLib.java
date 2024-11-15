package dev.iseal.sealLib;

import de.leonhard.storage.Config;
import dev.iseal.sealLib.Commands.DebugCommand;
import dev.iseal.sealLib.Metrics.MetricsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SealLib extends JavaPlugin {

    private final Config config = new Config("config", this.getDataFolder().getPath()+"/config/");
    private static boolean debug = false;
    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        Bukkit.getServer().getPluginManager().registerEvents(MetricsManager.getInstance(), this);
        debug = config.getOrSetDefault("debug", false);
        if (debug)
            Bukkit.getPluginCommand("debug").setExecutor(new DebugCommand());
    }

    @Override
    public void onDisable() {
        MetricsManager.getInstance().exitAndSendInfo();
    }

    public static boolean isDebug() {
        return debug;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

}
