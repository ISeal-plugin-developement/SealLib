package dev.iseal.sealLib.Systems.Gui.patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GuiPatternUtils {

    /**
     * Creates a border pattern for a GUI of given width and height.
     *
     * @param width  The width of the GUI (usually 9).
     * @param height The height of the GUI in rows.
     * @return A list of slot indices forming the border.
     */
    public static List<Integer> border(int width, int height) {
        List<Integer> slots = new ArrayList<>();
        // Top and bottom rows
        for (int i = 0; i < width; i++) {
            slots.add(i); // Top
            if (height > 1) {
                slots.add(width * (height - 1) + i); // Bottom
            }
        }
        // Left and right columns (excluding corners)
        for (int i = 1; i < height - 1; i++) {
            slots.add(i * width); // Left
            slots.add(i * width + width - 1); // Right
        }
        return slots;
    }

    /**
     * Creates a "marching ants" border pattern, where items appear to move along the border.
     * This is achieved by alternating items in slots.
     *
     * @param width  The width of the GUI (usually 9).
     * @param height The height of the GUI in rows.
     * @return A list of lists of slot indices. Each inner list is a frame of the animation.
     */
    @Deprecated
    public static List<List<Integer>> marchingAntsBorder(int width, int height) {
        List<Integer> borderSlots = border(width, height);
        List<List<Integer>> frames = new ArrayList<>();
        frames.add(IntStream.range(0, borderSlots.size())
                .filter(i -> i % 2 == 0)
                .mapToObj(borderSlots::get)
                .collect(Collectors.toList()));
        frames.add(IntStream.range(0, borderSlots.size())
                .filter(i -> i % 2 != 0)
                .mapToObj(borderSlots::get)
                .collect(Collectors.toList()));
        return frames;
    }

    /**
     * Creates a "sweeping" border pattern, where items appear to sweep across the border.
     *
     * @param width  The width of the GUI (usually 9).
     * @param height The height of the GUI in rows.
     * @return A list of lists of slot indices. Each inner list is a frame of the animation.
     */
    @Deprecated
    public static List<List<Integer>> sweepingBorder(int width, int height) {
        List<Integer> borderSlots = getOrderedBorderSlots(width, height);
        List<List<Integer>> frames = new ArrayList<>();
        for (Integer borderSlot : borderSlots) {
            frames.add(List.of(borderSlot));
        }
        return frames;
    }

    /**
     * Creates a "looping" border pattern, where a single item travels along the border in a loop.
     *
     * @param width  The width of the GUI (usually 9).
     * @param height The height of the GUI in rows.
     * @return A list of lists of slot indices. Each inner list is a frame of the animation, containing one slot.
     */
    @Deprecated
    public static List<List<Integer>> loopingBorder(int width, int height) {
        List<Integer> borderSlots = getOrderedBorderSlots(width, height);
        List<List<Integer>> frames = new ArrayList<>();
        for (Integer borderSlot : borderSlots) {
            frames.add(List.of(borderSlot));
        }
        return frames;
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
