package dev.iseal.sealLib.Systems.Gui.patterns.impl;

import dev.iseal.sealLib.Systems.Gui.patterns.AnimatedPattern;
import dev.iseal.sealLib.Systems.Gui.patterns.GuiPatternUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MarchingAntsBorderPattern extends AnimatedPattern {

    public MarchingAntsBorderPattern(int width, int height) {
        super(generateFrames(width, height));
    }

    private static List<List<Integer>> generateFrames(int width, int height) {
        List<Integer> borderSlots = GuiPatternUtils.border(width, height);
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