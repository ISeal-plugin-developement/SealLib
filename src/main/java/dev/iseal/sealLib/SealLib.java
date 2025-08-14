package dev.iseal.sealLib;

import de.leonhard.storage.Config;
import dev.iseal.ExtraKryoCodecs.Enums.SerializersEnums.AnalyticsAPI.AnalyticsSerializers;
import dev.iseal.ExtraKryoCodecs.Holders.AnalyticsAPI.PluginVersionInfo;
import dev.iseal.sealLib.Commands.DebugCommand;
import dev.iseal.sealLib.Metrics.MetricsManager;
import dev.iseal.sealUtils.SealUtils;
import dev.iseal.sealUtils.systems.analytics.AnalyticsManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Random;

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
        boolean metricsEnabled = config.getOrSetDefault("metricsEnabled", true);
        SealUtils.init(debug, this.getDescription().getVersion());
        config.setDefault("updaterAllowBeta", false);
        config.setDefault("updaterAllowAlpha", false);
        Random random = new Random();
        String aaid = config.getOrSetDefault("analyticsID", "AA-"+random.nextInt(100000000, 999999999)+1);
        if (debug)
            Bukkit.getPluginCommand("debug").setExecutor(new DebugCommand());
        checkSoftDependencies();
        if (metricsEnabled) {
            MetricsManager.getInstance().addMetrics(this, 24183);
            AnalyticsManager.INSTANCE.setEnabled("SealLib", true);
            PluginDescriptionFile pdf = this.getDescription();
            AnalyticsManager.INSTANCE.sendEvent(
                aaid,
                AnalyticsSerializers.PLUGIN_VERSION_INFO,
                new PluginVersionInfo(
                    pdf.getVersion(), // pluginVersion
                    Bukkit.getServer().getVersion(), // serverVersion
                    Bukkit.getServer().getName(), // serverSoftware
                    System.getProperty("java.version"), // serverJavaVersion
                    System.getProperty("os.name"), // serverOS
                    System.getProperty("os.version"), // serverOSVersion
                    System.getProperty("os.arch") // serverArchitecture
                )
            );
        }
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
