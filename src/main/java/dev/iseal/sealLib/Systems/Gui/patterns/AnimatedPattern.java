package dev.iseal.sealLib.Systems.Gui.patterns;

import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import dev.iseal.sealLib.Systems.Gui.inventory.AbstractGui;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class AnimatedPattern extends Pattern {

    protected final List<List<Integer>> frames;
    private int currentFrame = 0;

    public AnimatedPattern(List<List<Integer>> frames) {
        super(null); // Animated patterns don't have a single static list of slots
        this.frames = frames;
    }

    @Override
    public void applyPattern(AbstractGui gui, Component component) {
        if (frames == null || frames.isEmpty()) return;
        gui.setComponent(frames.get(currentFrame), component);
    }

    public void nextFrame(AbstractGui gui, Component component, Component background) {
        if (frames == null || frames.isEmpty()) return;

        // Clear previous frame
        List<Integer> previousFrameSlots = frames.get(currentFrame);
        gui.setComponent(previousFrameSlots, background);

        // Move to next frame
        currentFrame = (currentFrame + 1) % frames.size();

        // Apply new frame
        applyPattern(gui, component);
    }
}

