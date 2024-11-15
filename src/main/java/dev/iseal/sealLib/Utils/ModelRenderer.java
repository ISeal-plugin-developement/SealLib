package dev.iseal.sealLib.Utils;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class ModelRenderer {

    public static List<Vector> getVectors(String URL, float scale, float rotationAngle, float precision) throws IOException {
        InputStream modelStream = new File(URL).toURI().toURL().openStream();
        List<Vector> modelVertices = new ArrayList<>();
        try {
            modelVertices = loadModel(modelStream, scale, rotationAngle, precision);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to load the 3D model");
            ExceptionHandler.getInstance().dealWithException(e, Level.SEVERE, "MODEL_LOADING_FAILED", URL);
        }
        return modelVertices;
    }

    private static List<Vector> loadModel(InputStream file, float scale, float rotationAngle, float precision) throws IOException {

        try (InputStream inputStream = file) {
            Obj obj = ObjReader.read(inputStream);
            Obj normalizedObj = ObjUtils.convertToRenderable(obj);
            List<Vector> vertices = new ArrayList<>();

            // Create a Set to store unique indices of surface vertices
            Set<Integer> surfaceVertexIndices = new HashSet<>();

            // Iterate over all faces and add vertex indices to the Set
            for (int i = 0; i < normalizedObj.getNumFaces(); i++) {
                for (int j = 0; j < normalizedObj.getFace(i).getNumVertices(); j++) {
                    surfaceVertexIndices.add(normalizedObj.getFace(i).getVertexIndex(j));
                }
            }

            // Iterate over the unique surface vertex indices and add the corresponding vertices to the List
            for (int index : surfaceVertexIndices) {
                float x = normalizedObj.getVertex(index).getX() * scale;
                float y = normalizedObj.getVertex(index).getY() * scale;
                float z = normalizedObj.getVertex(index).getZ() * scale;

                // Apply rotation around the Y-axis
                float rotatedX = (float) (x * Math.cos(rotationAngle) - z * Math.sin(rotationAngle));
                float rotatedZ = (float) (x * Math.sin(rotationAngle) + z * Math.cos(rotationAngle));

                // Add vertices based on precision
                if (vertices.isEmpty() || vertices.stream().noneMatch(v -> v.distance(new Vector(rotatedX, y, rotatedZ)) < precision)) {
                    vertices.add(new Vector(rotatedX, y, rotatedZ));
                }
            }

            return vertices;
        }
    }
}