package dev.iseal.sealLib.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class BlockDisplayUtil {

    public static void createBlockDisplay(World world, Location location, Material material, float scale) {
        BlockDisplay blockDisplay = (BlockDisplay) world.spawn(location, BlockDisplay.class);
        blockDisplay.setBlock(material.createBlockData());
        // Create transformation components
        Vector3f translation = new Vector3f(0f, 0f, 0f);
        Vector3f scaleVector = new Vector3f(scale, scale, scale);
        Transformation transformation = new Transformation(translation, new Quaternionf(), scaleVector, new Quaternionf());

        blockDisplay.setTransformation(transformation);
    }

    public static void renderModel(Location location, List<Vector> vertices, Material material, float scale) {
        World world = location.getWorld();
        for (Vector vertex : vertices) {
            Location vertexLocation = location.clone().add(vertex);
            createBlockDisplay(world, vertexLocation, material, scale);
        }
    }
}