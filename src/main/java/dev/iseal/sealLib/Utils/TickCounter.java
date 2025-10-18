package dev.iseal.sealLib.Utils;

import dev.iseal.sealLib.SealLib;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

public class TickCounter {
    /**
     * -- GETTER --
     *  Fetches the current server tick from a custom counter.
     *  This is independent of world time.
     *
     * @return The current server tick.
     */
    @Getter
    private static long currentTick = 0;

    static {
        new BukkitRunnable() {
            @Override
            public void run() {
                currentTick++;
            }
        }.runTaskTimer(SealLib.getPlugin(), 0L, 1L);
    }
}
