package dev.iseal.sealLib.Api;

import dev.iseal.sealLib.Metrics.MetricsManager;
import dev.iseal.sealLib.Utils.ExceptionHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Consumer;

public class ApiManager {

    private static ApiManager instance;
    public static ApiManager getInstance() {
        if (instance == null)
            instance = new ApiManager();
        return instance;
    }

    private final MetricsManager metricsManager = MetricsManager.getInstance();
    
    public void registerJoinMetrics(Consumer<Player> consumer) {
        metricsManager.addJoinMetrics(consumer);
    }

    public void registerQuitMetrics(Consumer<Player> consumer) {
        metricsManager.addQuitMetrics(consumer);
    }

    public void registerShutdownMetrics(Consumer<Player> consumer) {
        metricsManager.addShutdownMetrics(consumer);
    }

    /*
    * Register a package to be scanned for dumpables
    *
    * ! REMEMBER TO DO THIS OR YOUR INFO WILL NOT BE DUMPED !
     */
    public void registerExceptionHandlerPackage(String packageName) {
        ExceptionHandler.getInstance().registerPackage(packageName);
    }

    public void registerBStatsMetrics(JavaPlugin plugin, int id) {
        metricsManager.addMetrics(plugin, id);
    }
}
