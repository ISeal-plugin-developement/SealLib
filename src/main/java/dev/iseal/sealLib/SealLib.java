package dev.iseal.sealLib;

import de.leonhard.storage.Config;
import dev.iseal.sealLib.Metrics.MetricsManager;
import dev.iseal.sealLib.Utils.ExceptionHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SealLib extends JavaPlugin {

    private final Config config = new Config("config", this.getDataFolder().getPath()+"/config/");
    private static boolean debug = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getServer().getPluginManager().registerEvents(MetricsManager.getInstance(), this);
        debug = config.getOrSetDefault("debug", false);
    }

    @Override
    public void onDisable() {
        MetricsManager.getInstance().exitAndSendInfo();
    }

    public static boolean isDebug() {
        return debug;
    }

}
