package dev.iseal.sealLib.Systems.Gui.patterns.impl;

import dev.iseal.sealLib.Systems.Gui.patterns.AnimatedPattern;
import dev.iseal.sealLib.Systems.Gui.patterns.GuiPatternUtils;

import java.util.ArrayList;
import java.util.List;

public class LoopingBorderPattern extends AnimatedPattern {

    public LoopingBorderPattern(int width, int height) {
        this(width, height, 10);
    }

    public LoopingBorderPattern(int width, int height, int updateIntervalTicks) {
        super(generateFrames(width, height), updateIntervalTicks);
    }

    private static List<List<Integer>> generateFrames(int width, int height) {
        List<Integer> borderSlots = GuiPatternUtils.getOrderedBorderSlots(width, height);
        List<List<Integer>> frames = new ArrayList<>();
        for (Integer borderSlot : borderSlots) {
            frames.add(List.of(borderSlot));
        }
        return frames;
    }
}
