package dev.iseal.sealLib.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class OptimizedBlockDisplayUtil {

    // Maximum distance between points to be considered for batching
    private static final double BATCH_THRESHOLD = 0.8;
    // Minimum number of points to form a batch
    private static final int MIN_BATCH_SIZE = 4;
    
    /**
     * Creates a single block display with custom scale and position
     */
    public static BlockDisplay createBlockDisplay(World world, Location location, Material material, 
                                                 Vector3f scale, Vector3f translation) {
        BlockDisplay blockDisplay = world.spawn(location, BlockDisplay.class);
        blockDisplay.setBlock(material.createBlockData());
        
        Transformation transformation = new Transformation(
            translation, 
            new Quaternionf(), 
            scale, 
            new Quaternionf()
        );
        
        blockDisplay.setTransformation(transformation);
        return blockDisplay;
    }
    
    /**
     * Renders a model with optimized batching - large sections use single entities,
     * while detailed areas use individual blocks
     */
    public static void renderOptimizedModel(Location baseLocation, List<Vector> vertices, Material material, float blockScale) {
        World world = baseLocation.getWorld();
        if (world == null || vertices.isEmpty()) return;
        
        // Group vertices into batches based on proximity
        List<List<Vector>> batches = batchVertices(vertices);
        
        for (List<Vector> batch : batches) {
            if (batch.size() == 1) {
                // Single point - render as individual block
                Vector vertex = batch.get(0);
                Location vertexLocation = baseLocation.clone().add(vertex);
                createBlockDisplay(world, vertexLocation, material, 
                                   new Vector3f(blockScale, blockScale, blockScale),
                                   new Vector3f(0f, 0f, 0f));
            } else {
                // Batch of points - create a larger block that covers the area
                renderBatch(world, baseLocation, batch, material, blockScale);
            }
        }
    }
    
    /**
     * Renders a batch of vertices using a single entity
     */
    private static void renderBatch(World world, Location baseLocation, List<Vector> batch, Material material, float blockScale) {
        // Calculate bounds of the batch
        Vector min = new Vector(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Vector max = new Vector(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
        
        for (Vector v : batch) {
            min.setX(Math.min(min.getX(), v.getX()));
            min.setY(Math.min(min.getY(), v.getY()));
            min.setZ(Math.min(min.getZ(), v.getZ()));
            
            max.setX(Math.max(max.getX(), v.getX()));
            max.setY(Math.max(max.getY(), v.getY()));
            max.setZ(Math.max(max.getZ(), v.getZ()));
        }
        
        // Calculate center and dimensions of the batch
        Vector center = min.clone().add(max).multiply(0.5);
        Vector dimensions = max.clone().subtract(min);
        
        // Create a single block display for the batch
        Location batchLocation = baseLocation.clone().add(center);
        Vector3f scale = new Vector3f(
            (float)(dimensions.getX() + blockScale),
            (float)(dimensions.getY() + blockScale),
            (float)(dimensions.getZ() + blockScale)
        );
        
        createBlockDisplay(world, batchLocation, material, scale, new Vector3f(0f, 0f, 0f));
    }
    
    /**
     * Groups vertices into batches based on proximity
     */
    private static List<List<Vector>> batchVertices(List<Vector> vertices) {
        List<List<Vector>> batches = new ArrayList<>();
        Set<Vector> processedPoints = new HashSet<>();
        
        for (Vector point : vertices) {
            if (processedPoints.contains(point)) continue;
            
            List<Vector> batch = new ArrayList<>();
            batch.add(point);
            processedPoints.add(point);
            
            // Find nearby points
            for (Vector candidate : vertices) {
                if (processedPoints.contains(candidate)) continue;
                
                if (point.distance(candidate) <= BATCH_THRESHOLD) {
                    batch.add(candidate);
                    processedPoints.add(candidate);
                }
            }
            
            // If batch is too small, don't batch it
            if (batch.size() < MIN_BATCH_SIZE && batch.size() > 1) {
                for (Vector v : batch) {
                    List<Vector> singlePointBatch = new ArrayList<>();
                    singlePointBatch.add(v);
                    batches.add(singlePointBatch);
                }
            } else {
                batches.add(batch);
            }
        }
        
        return batches;
    }
    
    /**
     * Level of Detail (LOD) rendering - use fewer entities at greater distances
     */
    public static void renderModelWithLOD(Location baseLocation, List<Vector> vertices, Material material,
                                          float blockScale, Player viewer, double maxDistance) {
        double distance = viewer.getLocation().distance(baseLocation);
        
        if (distance > maxDistance * 0.7) {
            // Far distance - use very simplified representation
            List<Vector> simplifiedVertices = simplifyMesh(vertices, 0.25f); 
            renderOptimizedModel(baseLocation, simplifiedVertices, material, blockScale * 1.5f);
        } else if (distance > maxDistance * 0.4) {
            // Medium distance - use somewhat simplified representation
            List<Vector> simplifiedVertices = simplifyMesh(vertices, 0.5f);
            renderOptimizedModel(baseLocation, simplifiedVertices, material, blockScale * 1.2f);
        } else {
            // Close distance - use full detail
            renderOptimizedModel(baseLocation, vertices, material, blockScale);
        }
    }
    
    /**
     * Simple mesh simplification by sampling
     */
    private static List<Vector> simplifyMesh(List<Vector> vertices, float ratio) {
        if (ratio >= 1.0f) return new ArrayList<>(vertices);
        
        int targetCount = Math.max(1, (int)(vertices.size() * ratio));
        List<Vector> result = new ArrayList<>(targetCount);
        
        // Simple sampling approach
        int step = Math.max(1, vertices.size() / targetCount);
        for (int i = 0; i < vertices.size(); i += step) {
            result.add(vertices.get(i));
        }
        
        return result;
    }
}

