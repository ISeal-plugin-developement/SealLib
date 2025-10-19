package dev.iseal.sealLib.Systems.Gui.patterns.impl;

import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import dev.iseal.sealLib.Systems.Gui.inventory.AbstractGui;
import dev.iseal.sealLib.Systems.Gui.patterns.AnimatedPattern;
import dev.iseal.sealLib.Systems.Gui.patterns.GuiPatternUtils;

import java.util.ArrayList;
import java.util.List;

public class SweepingBorderPattern extends AnimatedPattern {

    private Component component1;
    private Component component2;
    private final List<Integer> allBorderSlots;

    public SweepingBorderPattern(int width, int height) {
        this(width, height, 2, 0);
    }

    public SweepingBorderPattern(int width, int height, int updateIntervalTicks) {
        this(width, height, updateIntervalTicks, 0);
    }

    public SweepingBorderPattern(int width, int height, int updateIntervalTicks, int borderOffset) {
        super(generateFrames(width, height, borderOffset), updateIntervalTicks, borderOffset);
        this.allBorderSlots = GuiPatternUtils.getOrderedBorderSlots(width, height, borderOffset);
    }

    @Override
    public void apply(AbstractGui gui, Component component) {
        // This ensures applying with a single component still works
        if (this.component1 == null) this.component1 = component;
        if (this.component2 == null) this.component2 = component;
        apply(gui);
    }

    public void apply(AbstractGui gui) {
        if (component1 == null || component2 == null) return;

        // Get current frame index
        int frameIndex = getCurrentFrame();
        int totalFrames = getFrames().size();
        int halfWayPoint = totalFrames / 2;

        // Determine which component is sweeping based on which half of the animation we're in
        boolean firstHalf = frameIndex < halfWayPoint;

        if (firstHalf) {
            // First half: component1 as background, component2 as sweeper
            gui.setComponent(allBorderSlots, component1);
            gui.setComponent(getFrames().get(frameIndex), component2);
        } else {
            // Second half: component2 as background, component1 as sweeper
            gui.setComponent(allBorderSlots, component2);
            gui.setComponent(getFrames().get(frameIndex), component1);
        }
    }

    /**
     * Sets the components for this sweeping border pattern.
     * @param component1 The background component
     * @param component2 The sweeping component
     */
    public void setComponents(Component component1, Component component2) {
        this.component1 = component1;
        this.component2 = component2;
    }

    /**
     * Generate frames for the sweeping border animation.
     * Each frame represents the positions of the sweeping component.
     */
    private static List<List<Integer>> generateFrames(int width, int height, int borderOffset) {
        List<Integer> borderSlots = GuiPatternUtils.getOrderedBorderSlots(width, height, borderOffset);

        // Find middle-left point to start the sweep
        int startingPoint = findStartingPoint(width, height, borderSlots, borderOffset);

        // Reorder border slots to start from the middle-left point
        List<Integer> orderedBorderSlots = reorderBorderSlots(borderSlots, startingPoint);

        // Generate frames where each successive frame adds one more slot to the sweep
        List<List<Integer>> frames = new ArrayList<>();

        // First half: component2 sweeping over component1
        for (int i = 1; i <= orderedBorderSlots.size(); i++) {
            List<Integer> sweepSlots = new ArrayList<>(orderedBorderSlots.subList(0, i));
            frames.add(sweepSlots);
        }

        // Second half: component1 sweeping over component2
        for (int i = 1; i <= orderedBorderSlots.size(); i++) {
            List<Integer> sweepSlots = new ArrayList<>(orderedBorderSlots.subList(0, i));
            frames.add(sweepSlots);
        }

        return frames;
    }

    /**
     * Find the middle-left point of the border to start the sweep.
     */
    private static int findStartingPoint(int width, int height, List<Integer> borderSlots, int offset) {
        // For a chest GUI with width 9, the left-center position would be:
        int leftCol = offset;
        int middleRow = height / 2; // Integer division, for odd heights this is perfect, for even it rounds down
        int startSlot = middleRow * width + leftCol;

        // Find the closest border slot to this theoretical position
        int closestIndex = 0;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < borderSlots.size(); i++) {
            int slot = borderSlots.get(i);
            int slotRow = slot / width;
            int slotCol = slot % width;

            // Calculate distance - heavily weight the column to prioritize leftmost position
            int distance = Math.abs(slotRow - middleRow) * 100 + slotCol;

            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    /**
     * Reorder border slots to start from the specified starting point.
     */
    private static List<Integer> reorderBorderSlots(List<Integer> borderSlots, int startingPoint) {
        List<Integer> reordered = new ArrayList<>();

        // Add slots from starting point to the end
        for (int i = startingPoint; i < borderSlots.size(); i++) {
            reordered.add(borderSlots.get(i));
        }

        // Add slots from the beginning to the starting point
        for (int i = 0; i < startingPoint; i++) {
            reordered.add(borderSlots.get(i));
        }

        return reordered;
    }
}
