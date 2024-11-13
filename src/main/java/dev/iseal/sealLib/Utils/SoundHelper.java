package dev.iseal.sealLib.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SoundHelper {

    public static void playSoundForPlayer(String namespace, String sound, Player plr, float pitch) {
        // Play sound
        plr.playSound(plr, namespace + ":" + sound, 1, pitch);
    }

    public static void playSoundAroundPlayer(String namespace, String sound, Player plr, float pitch) {
        // Play sound
        plr.getWorld().playSound(plr.getLocation(), namespace + ":" + sound, 3, pitch);
    }

    public static void playSoundForAllPlayers(String namespace, String sound, float pitch) {
        // Play sound
        for (Player plr : Bukkit.getOnlinePlayers()) {
            plr.playSound(plr, namespace + ":" + sound, 1, pitch);
        }
    }

    public static void playSoundInWorld(String namespace, String sound, float pitch, Location loc) {
        // Play sound
        loc.getWorld().playSound(loc, namespace + ":" + sound, 10^20, pitch);
    }

    public static void playSoundAroundEntity(String namespace, String sound, float pitch, Location loc) {
        // Play sound
        loc.getWorld().playSound(loc, namespace + ":" + sound, 3, pitch);
    }

}
