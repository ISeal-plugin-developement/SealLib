package dev.iseal.sealLib.Systems.Gui.patterns.impl;

import dev.iseal.sealLib.Systems.Gui.patterns.AnimatedPattern;
import dev.iseal.sealLib.Systems.Gui.patterns.GuiPatternUtils;

import java.util.ArrayList;
import java.util.List;

public class LoopingBorderPattern extends AnimatedPattern {

    public LoopingBorderPattern(int width, int height) {
        this(width, height, 10, 0);
    }

    public LoopingBorderPattern(int width, int height, int updateIntervalTicks) {
        this(width, height, updateIntervalTicks, 0);
    }
    
    public LoopingBorderPattern(int width, int height, int updateIntervalTicks, int borderOffset) {
        super(generateFrames(width, height, borderOffset), updateIntervalTicks, borderOffset);
    }

    private static List<List<Integer>> generateFrames(int width, int height) {
        return generateFrames(width, height, 0);
    }
    
    private static List<List<Integer>> generateFrames(int width, int height, int borderOffset) {
        List<Integer> borderSlots = GuiPatternUtils.getOrderedBorderSlots(width, height, borderOffset);
        List<List<Integer>> frames = new ArrayList<>();
        for (Integer borderSlot : borderSlots) {
            frames.add(List.of(borderSlot));
        }
        return frames;
    }
}
