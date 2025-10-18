package dev.iseal.sealLib.Systems.Gui.patterns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GuiPatternUtils {

    public static List<Integer> border(int width, int height) {
        Set<Integer> borderSlots = new HashSet<>();
        if (height <= 0 || width <= 0) return new ArrayList<>();
        // Top and bottom rows
        for (int i = 0; i < width; i++) {
            borderSlots.add(i); // Top
            borderSlots.add(width * (height - 1) + i); // Bottom
        }
        // Left and right columns
        for (int i = 0; i < height; i++) {
            borderSlots.add(i * width); // Left
            borderSlots.add(i * width + width - 1); // Right
        }
        return new ArrayList<>(borderSlots);
    }

    public static List<Integer> getOrderedBorderSlots(int width, int height) {
        List<Integer> borderSlots = new ArrayList<>();
        if (height <= 0 || width <= 0) return borderSlots;
        // Top row
        for (int i = 0; i < width; i++) borderSlots.add(i);
        if (height > 1) {
            // Right column
            for (int i = 1; i < height; i++) borderSlots.add(i * width + width - 1);
            if (width > 1) {
                // Bottom row (reversed)
                for (int i = width - 2; i >= 0; i--) borderSlots.add(width * (height - 1) + i);
                // Left column (reversed)
                for (int i = height - 2; i > 0; i--) borderSlots.add(i * width);
            }
        }
        return borderSlots;
    }
}
