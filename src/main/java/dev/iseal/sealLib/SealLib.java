package dev.iseal.sealLib;

import dev.iseal.sealLib.Metrics.MetricsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SealLib extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getServer().getPluginManager().registerEvents(MetricsManager.getInstance(), this);
    }

    @Override
    public void onDisable() {
        MetricsManager.getInstance().exitAndSendInfo();
    }
}
