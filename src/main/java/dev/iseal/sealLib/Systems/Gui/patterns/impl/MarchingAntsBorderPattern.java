package dev.iseal.sealLib.Systems.Gui.patterns.impl;

import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import dev.iseal.sealLib.Systems.Gui.inventory.AbstractGui;
import dev.iseal.sealLib.Systems.Gui.patterns.AnimatedPattern;
import dev.iseal.sealLib.Systems.Gui.patterns.GuiPatternUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MarchingAntsBorderPattern extends AnimatedPattern {

    private Component component1;
    private Component component2;

    public MarchingAntsBorderPattern(int width, int height, int updateIntervalTicks, Component component1, Component component2) {
        this(width, height, updateIntervalTicks, 0, component1, component2);
    }
    
    public MarchingAntsBorderPattern(int width, int height, int updateIntervalTicks, int borderOffset, 
                                    Component component1, Component component2) {
        super(generateFrames(width, height, borderOffset), updateIntervalTicks);
        this.component1 = component1;
        this.component2 = component2;
    }

    @Override
    public void apply(AbstractGui gui, Component component) {
        // This override ensures that applying with a single component still works,
        // but for the intended effect, the other apply method should be used.
        if (this.component1 == null) this.component1 = component;
        if (this.component2 == null) this.component2 = component;
        apply(gui);
    }

    public void apply(AbstractGui gui) {
        if (component1 == null || component2 == null) return;

        // Use the animated pattern's current frame index (advanced by nextFrame)
        // to determine which component should be drawn on which set of border slots.
        int frameIndex = getCurrentFrame();
        Component first = (frameIndex % 2 == 0) ? component1 : component2;
        Component second = (frameIndex % 2 == 0) ? component2 : component1;

        gui.setComponent(getFrames().get(0), first);
        gui.setComponent(getFrames().get(1), second);
    }

    private static List<List<Integer>> generateFrames(int width, int height, int borderOffset) {
        List<Integer> borderSlots = GuiPatternUtils.getOrderedBorderSlots(width, height, borderOffset); // ordered
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
}
