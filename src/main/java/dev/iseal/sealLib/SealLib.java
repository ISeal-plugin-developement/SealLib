package dev.iseal.sealLib;

import de.leonhard.storage.Config;
import dev.iseal.sealLib.Commands.DebugCommand;
import dev.iseal.sealLib.Metrics.MetricsManager;
import dev.iseal.sealLib.Systems.Effekts.Effekt;
import dev.iseal.sealLib.Systems.Effekts.Serializers.ScreenshakeSerializer;
import dev.iseal.sealLib.Utils.UnsafeSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

import java.util.ArrayList;

public final class SealLib extends JavaPlugin {

    private final Config config = new Config("config", this.getDataFolder().getPath()+"/config/");
    private static boolean debug = false;
    private static JavaPlugin plugin;

    private static final ArrayList<String> loadedDependencies = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        Bukkit.getServer().getPluginManager().registerEvents(MetricsManager.getInstance(), this);
        debug = config.getOrSetDefault("debug", false);
        if (debug)
            Bukkit.getPluginCommand("debug").setExecutor(new DebugCommand());
        checkSoftDependencies();
        /*
        for (Effekt effekt : Effekt.values()) {
            UnsafeSerializer.registerClass(effekt.getEffectClass(), effekt.getID());
        }*/
        /*
        for (Effekt effekt : Effekt.values()) {
            kryo.register(effekt.getEffectClass(), effekt.getSerializer() ,effekt.getID());
        }*/
        MetricsManager.getInstance().addMetrics(this, 24183);
        UnsafeSerializer.registerClass(ScreenshakeInstance.class, new ScreenshakeSerializer(), Effekt.SCREENSHAKE.getID());
    }

    @Override
    public void onDisable() {
        MetricsManager.getInstance().exitAndSendInfo();
    }

    private void checkSoftDependencies() {
        for (String dependency : plugin.getDescription().getSoftDepend()) {
            if (debug) getLogger().info("[SealLib] Checking Soft Dependency " + dependency);
            if (Bukkit.getPluginManager().getPlugin(dependency) != null) {
                loadedDependencies.add(dependency);
                if (debug)
                    getLogger().info("[SealLib] Soft Dependency " + dependency + " found!");
            } else {
                getLogger().warning("[SealLib] Soft Dependency " + dependency + " not found! Some features may not work.");
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

}
