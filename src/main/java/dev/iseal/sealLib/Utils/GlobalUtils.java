package dev.iseal.sealLib.Utils;

import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.reflections.Reflections;

import java.util.Comparator;
import java.util.Set;

public class GlobalUtils {

    public static Set<Class<?>> findAllClassesInPackage(String packageName, Class<?> clazz) {
        Reflections reflections = new Reflections(packageName);
        return (Set<Class<?>>) reflections.getSubTypesOf(clazz);
    }

    public static Entity raycastPrecise(Player entity, double range) {
        RayTraceResult result = entity.getWorld().rayTraceEntities(
                entity.getEyeLocation(),
                entity.getEyeLocation().getDirection(),
                range,
                0.1,
                (en) -> en.getEntityId() != entity.getEntityId()
        );
        if (result == null) return null;
        return result.getHitEntity();
    }

    public static Entity raycastInaccurate(Player entity, double range) {
        RayTraceResult result = entity.getWorld().rayTraceEntities(
                entity.getEyeLocation(),
                entity.getEyeLocation().getDirection(),
                range,
                1.5,
                (en) -> en.getEntityId() != entity.getEntityId()
        );
        if (result != null) return result.getHitEntity();
        RayTraceResult result2 = entity.getWorld().rayTraceBlocks(
                entity.getEyeLocation(),
                entity.getEyeLocation().getDirection(),
                range,
                FluidCollisionMode.SOURCE_ONLY,
                true
        );
        if (result2 == null) return null;
        return entity.getWorld().getNearbyEntities(result2.getHitPosition().toLocation(entity.getWorld()), 1.5, 1.5, 1.5)
                .stream()
                .filter((en) -> en.getEntityId() != entity.getEntityId())
                .min(Comparator.comparingDouble(en -> en.getLocation().distance(entity.getLocation())))
                .orElse(null);
    }
}
