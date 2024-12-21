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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.iseal.sealLib.SealLib.isDebug;

public class GlobalUtils {

    /*
        * Serialize a serializable class to a byte array
        * @param obj The object to serialize
        * @return The serialized object
        * @throws IllegalArgumentException If the object is not serializable
     */
    public static byte[] serializeSerializableClass(final Object obj, final byte[] extraData) {
        if (!(obj instanceof Serializable)) {
            throw new IllegalArgumentException("Object must be serializable");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            // write extra data to the beginning of the stream
            out.write(extraData);

            // write the object to the stream
            out.writeObject(obj);
            out.flush();
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Object deserializeSerializableClass(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

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
        if (!isDebug()) {
            throw new UnsupportedOperationException("This method is not implemented yet.");
        }

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
                .collect(Collectors.toList());

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

    public static String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789_.-";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (chars.length() * Math.random());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

}
