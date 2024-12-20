package dev.iseal.sealLib.Utils;

import org.bukkit.FluidCollisionMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.reflections.Reflections;

import java.util.Comparator;
import java.util.Set;

public class GlobalUtils {

    public static Set<Class<?>> findAllClassesInPackage(String packageName, Class<?> clazz) {
        Thread.currentThread().setContextClassLoader(clazz.getClassLoader());
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> classes = (Set<Class<?>>) reflections.getSubTypesOf(clazz);
        Thread.currentThread().setContextClassLoader(GlobalUtils.class.getClassLoader());
        return classes;
    }

    public static LivingEntity raycastPrecise(Player entity, double range) {
        RayTraceResult result = entity.getWorld().rayTraceEntities(
                entity.getEyeLocation(),
                entity.getEyeLocation().getDirection(),
                range,
                0.1,
                (en) -> en.getEntityId() != entity.getEntityId() && en instanceof LivingEntity
        );
        if (result == null) return null;
        return (LivingEntity) result.getHitEntity();
    }

    public static LivingEntity raycastInaccurate(Player entity, double range) {
        RayTraceResult result = entity.getWorld().rayTraceEntities(
                entity.getEyeLocation(),
                entity.getEyeLocation().getDirection(),
                range,
                1.5,
                (en) -> en.getEntityId() != entity.getEntityId() && en instanceof LivingEntity
        );
        if (result != null) return (LivingEntity) result.getHitEntity();
        RayTraceResult result2 = entity.getWorld().rayTraceBlocks(
                entity.getEyeLocation(),
                entity.getEyeLocation().getDirection(),
                range,
                FluidCollisionMode.SOURCE_ONLY,
                true
        );
        if (result2 == null) return null;
        return (LivingEntity) entity.getWorld().getNearbyEntities(result2.getHitPosition().toLocation(entity.getWorld()), 1.5, 1.5, 1.5)
                .stream()
                .filter((en) -> en.getEntityId() != entity.getEntityId() && en instanceof LivingEntity)
                .min(Comparator.comparingDouble(en -> en.getLocation().distance(entity.getLocation())))
                .orElse(null);
    }
}
