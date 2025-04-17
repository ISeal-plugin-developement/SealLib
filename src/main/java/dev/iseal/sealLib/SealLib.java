package dev.iseal.sealLib;

import de.leonhard.storage.Config;
import dev.iseal.ExtraKryoCodecs.ExtraKryoCodecs;
import dev.iseal.sealLib.Commands.DebugCommand;
import dev.iseal.sealLib.Metrics.MetricsManager;
import dev.iseal.sealLib.Utils.UnsafeSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.util.ArrayList;

public final class SealLib extends JavaPlugin {

    private static Config config;
    private static boolean debug = false;
    private static JavaPlugin plugin;

    private static final ArrayList<String> loadedDependencies = new ArrayList<>();
    private final String[] softDeps = {
            "ProtocolLib"
    };

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        config = new Config("config", this.getDataFolder().getPath()+"/config/");
        Bukkit.getServer().getPluginManager().registerEvents(MetricsManager.getInstance(), this);
        debug = config.getOrSetDefault("debug", false);
        config.setDefault("updaterAllowBeta", false);
        config.setDefault("updaterAllowAlpha", false);
        if (debug)
            Bukkit.getPluginCommand("debug").setExecutor(new DebugCommand());
        checkSoftDependencies();
        MetricsManager.getInstance().addMetrics(this, 24183);
    }

    @Override
    public void onDisable() {
        MetricsManager.getInstance().exitAndSendInfo();
    }

    private void checkSoftDependencies() {
        getLogger().info("Checking Soft Dependencies...");
        for (String dependency : softDeps) {
            if (debug) getLogger().info("Checking Soft Dependency " + dependency);
            if (Bukkit.getPluginManager().getPlugin(dependency) != null) {
                loadedDependencies.add(dependency);
                if (debug)
                    getLogger().info("Soft Dependency " + dependency + " found!");
            } else {
                getLogger().warning("Soft Dependency " + dependency + " not found! Some features may not work.");
            }
        }
    }

    public static boolean isDependencyLoaded(String dependency) {
        return loadedDependencies.contains(dependency);
    }

    public static boolean isDebug() {
        return debug;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static boolean isAllowBeta() {
        return config.getBoolean("updaterAllowBeta");
    }

    public static boolean isAllowAlpha() {
        return config.getBoolean("updaterAllowAlpha");
    }

}
