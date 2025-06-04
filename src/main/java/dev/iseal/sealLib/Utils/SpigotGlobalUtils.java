package dev.iseal.sealLib.Utils;

import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.reflections.Reflections;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SpigotGlobalUtils {

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

    public static Block raycastBlock(Player entity, double range) {
        RayTraceResult result = entity.getWorld().rayTraceBlocks(
                entity.getEyeLocation(),
                entity.getEyeLocation().getDirection(),
                range,
                FluidCollisionMode.SOURCE_ONLY,
                true
        );
        if (result == null) return null;
        return result.getHitBlock();
    }

    public static Vector getDirectionToEntity(Entity firstEntity, Entity secondEntity) {
        Vector playerEyeLocation = firstEntity.getLocation().toVector();
        Vector entityLocation = secondEntity.getLocation().toVector();
        return entityLocation.subtract(playerEyeLocation).normalize();
    }

    public static Vector getAccurateDirectionToEntity(LivingEntity firstEntity, Entity secondEntity) {
        Vector playerEyeLocation = firstEntity.getEyeLocation().toVector();
        Vector entityLocation = secondEntity.getLocation().toVector();
        return entityLocation.subtract(playerEyeLocation).normalize();
    }

    public static List<LivingEntity> getEntitiesInCone(Player player, double range, double fov) {
        Vector direction = player.getEyeLocation().getDirection().normalize();
        double halfFovRad = Math.toRadians(fov / 2);
        List<Entity> nearbyEntities = player.getNearbyEntities(range, range, range);

        // execute first step async
        List<LivingEntity> firstStep = nearbyEntities.parallelStream()
                // only allow living entities
                .filter(entity -> entity instanceof LivingEntity)
                // casting shenanigans
                .map(entity -> (LivingEntity) entity)
                // filter entities that are in the cone
                .filter(entity -> {
                    System.out.println("entity first step: " + entity.getName());
                    Vector toEntity = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                    double angle = direction.angle(toEntity);
                    return angle <= halfFovRad;
                })
                .toList();

        // Second step: filter entities that are in the line of sight
        // sync because bukkit api is not thread safe
        //TODO: fix this.
        List<LivingEntity> secondStep = firstStep.stream()
                .filter(entity -> {
                    Vector toEntity = entity.getEyeLocation().toVector().subtract(player.getEyeLocation().toVector()).normalize();

                    RayTraceResult result = player.getWorld().rayTraceEntities(
                            player.getEyeLocation(),
                            toEntity,
                            range,
                            0.1,
                            en -> en.getEntityId() != player.getEntityId() && en instanceof LivingEntity
                    );

                    System.out.println("entity second step: " + entity.getName() + " result: " + result);
                    return result != null && Objects.equals(result.getHitEntity(), entity);
                })
                .collect(Collectors.toList());

        return secondStep;
    }

}
