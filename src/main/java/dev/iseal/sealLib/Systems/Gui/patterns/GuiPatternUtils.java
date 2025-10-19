package dev.iseal.sealLib.Systems.Gui.patterns;

import java.util.ArrayList;
import java.util.List;

public class GuiPatternUtils {

    /**
     * Gets all slots that form a border of the GUI.
     * @param width Width of the GUI
     * @param height Height of the GUI
     * @return List of slot indices forming the border
     */
    public static List<Integer> getBorderSlots(int width, int height) {
        return getBorderSlots(width, height, 0);
    }

    /**
     * Gets all slots that form a border of the GUI with a specified offset.
     * @param width Width of the GUI
     * @param height Height of the GUI
     * @param offset Number of slots to offset from the edge (0 = outermost border)
     * @return List of slot indices forming the border
     */
    public static List<Integer> getBorderSlots(int width, int height, int offset) {
        List<Integer> slots = new ArrayList<>();
        
        if (offset >= width / 2 || offset >= height / 2) {
            throw new IllegalArgumentException("Offset too large for GUI dimensions");
        }
        
        // Top and bottom rows
        for (int x = offset; x < width - offset; x++) {
            slots.add(offset * width + x);                       // Top row
            slots.add((height - 1 - offset) * width + x);        // Bottom row
        }
        
        // Left and right columns (excluding corners which were added above)
        for (int y = offset + 1; y < height - 1 - offset; y++) {
            slots.add(y * width + offset);                       // Left column
            slots.add(y * width + (width - 1 - offset));         // Right column
        }
        
        return slots;
    }

    /**
     * Gets border slots in a specific order, starting from top-left and going clockwise.
     * @param width Width of the GUI
     * @param height Height of the GUI
     * @return Ordered list of border slot indices
     */
    public static List<Integer> getOrderedBorderSlots(int width, int height) {
        return getOrderedBorderSlots(width, height, 0);
    }

    /**
     * Gets border slots in a specific order with a specified offset, starting from top-left and going clockwise.
     * @param width Width of the GUI
     * @param height Height of the GUI
     * @param offset Number of slots to offset from the edge (0 = outermost border)
     * @return Ordered list of border slot indices
     */
    public static List<Integer> getOrderedBorderSlots(int width, int height, int offset) {
        List<Integer> slots = new ArrayList<>();
        
        if (offset >= width / 2 || offset >= height / 2) {
            throw new IllegalArgumentException("Offset too large for GUI dimensions");
        }
        
        // Top row (left to right)
        for (int x = offset; x < width - offset; x++) {
            slots.add(offset * width + x);
        }
        
        // Right column (top to bottom, excluding top-right corner)
        for (int y = offset + 1; y < height - offset; y++) {
            slots.add(y * width + (width - 1 - offset));
        }
        
        // Bottom row (right to left, excluding bottom-right corner)
        for (int x = width - 2 - offset; x >= offset; x--) {
            slots.add((height - 1 - offset) * width + x);
        }
        
        // Left column (bottom to top, excluding corners)
        for (int y = height - 2 - offset; y > offset; y--) {
            slots.add(y * width + offset);
        }
        
        return slots;
    }
}