package dev.iseal.sealLib.Metrics;

import com.google.gson.Gson;
import dev.iseal.sealLib.SealLib;
import dev.iseal.sealUtils.utils.ExceptionHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetricsManager implements Listener {

    private final ArrayList<Metrics> metricsList = new ArrayList<>();
    private final ArrayList<Consumer<Player>> joinMetrics = new ArrayList<>();
    private final ArrayList<Consumer<Player>> quitMetrics = new ArrayList<>();
    private final ArrayList<Consumer<Void>> shutdownMetrics = new ArrayList<>();
    private final HashMap<String, String> infoToSendOnExit = new HashMap<>();
    private static final Logger log = SealLib.getPlugin().getLogger();

    private static MetricsManager instance = null;
    public static MetricsManager getInstance() {
        if (instance == null) {
            instance = new MetricsManager();
        }
        return instance;
    }

    public void addJoinMetrics(Consumer<Player> consumer) {
        joinMetrics.add(consumer);
    }
    public void addQuitMetrics(Consumer<Player> consumer) {
        quitMetrics.add(consumer);
    }
    public void addShutdownMetrics(Consumer<Void> consumer) {
        shutdownMetrics.add(consumer);
    }
    public void addMetrics(JavaPlugin plugin, int id) {
        metricsList.add(new Metrics(plugin, id));
    }
    public void addInfoToSendOnExit(String endpoint, String gson) {
        infoToSendOnExit.put(endpoint, gson);
    }

    public void exitAndSendInfo() {
        metricsList.forEach(Metrics::shutdown);
            shutdownMetrics.forEach(consumer -> {
                try {
                    consumer.accept(null);
                } catch (Exception e) {
                    ExceptionHandler.getInstance().dealWithException(e, Level.WARNING, "CALL_SHUTDOWN_METRICS_FAILED", log);
                }
            });
        infoToSendOnExit.forEach((endpoint, info) -> {
            if (info == null) return;
            ConnectionManager.getInstance().sendDataToAPI(endpoint, info, "POST", true);
        });
    }



    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        quitMetrics.forEach(consumer -> {
            try {
                consumer.accept(e.getPlayer());
            } catch (Exception ex) {
                ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "CALL_QUIT_METRICS_FAILED", log);
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        joinMetrics.forEach(consumer -> {
            try {
                consumer.accept(e.getPlayer());
            } catch (Exception ex) {
                ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "CALL_JOIN_METRICS_FAILED", log);
            }
        });
    }

    public void sendError(String errorMessage, String packageName) {
        Gson gson = new Gson();
        ConnectionManager.getInstance().sendDataToAPI(packageName+"/errorcodes", gson.toJson(errorMessage), "POST", true);
    }
}
