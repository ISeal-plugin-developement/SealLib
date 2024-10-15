package dev.iseal.sealLib.Metrics;

import com.google.gson.Gson;
import dev.iseal.sealLib.Utils.ExceptionHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.logging.Level;

public class MetricsManager implements Listener {

    private final ArrayList<Metrics> metricsList = new ArrayList<>();
    private final ArrayList<Consumer<Player>> joinMetrics = new ArrayList<>();
    private final ArrayList<Consumer<Player>> quitMetrics = new ArrayList<>();
    private final ArrayList<Consumer<Player>> shutdownMetrics = new ArrayList<>();

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
    public void addShutdownMetrics(Consumer<Player> consumer) {
        shutdownMetrics.add(consumer);
    }
    public void addMetrics(JavaPlugin plugin, int id) {
        metricsList.add(new Metrics(plugin, id));
    }

    public void exitAndSendInfo() {
        metricsList.forEach(Metrics::shutdown);

        Bukkit.getServer().getOnlinePlayers().forEach(plr -> {
            quitMetrics.forEach(consumer -> {
                try {
                    consumer.accept(plr);
                } catch (Exception e) {
                    ExceptionHandler.getInstance().dealWithException(e, Level.WARNING, "CALL_QUIT_METRICS_FAILED");
                }
            });
            shutdownMetrics.forEach(consumer -> {
                try {
                    consumer.accept(plr);
                } catch (Exception e) {
                    ExceptionHandler.getInstance().dealWithException(e, Level.WARNING, "CALL_SHUTDOWN_METRICS_FAILED");
                }
            });
        });

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        quitMetrics.forEach(consumer -> {
            try {
                consumer.accept(e.getPlayer());
            } catch (Exception ex) {
                ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "CALL_QUIT_METRICS_FAILED");
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        joinMetrics.forEach(consumer -> {
            try {
                consumer.accept(e.getPlayer());
            } catch (Exception ex) {
                ExceptionHandler.getInstance().dealWithException(ex, Level.WARNING, "CALL_JOIN_METRICS_FAILED");
            }
        });
    }

    public void sendError(String errorMessage, String packageName) {
        Gson gson = new Gson();
        ConnectionManager.getInstance().sendData(packageName+"/errorcodes", gson.toJson(errorMessage), "POST");
    }
}
