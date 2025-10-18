package dev.iseal.sealLib.Systems.Gui.patterns;

import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import dev.iseal.sealLib.Systems.Gui.inventory.AbstractGui;
import dev.iseal.sealLib.Utils.TickCounter;
import lombok.Getter;

import java.util.List;

@Getter
public abstract class AnimatedPattern extends Pattern {

    protected final List<List<Integer>> frames;
    private int currentFrame = 0;
    private long lastUpdate;
    private final long updateInterval;

    public AnimatedPattern(List<List<Integer>> frames, int updateIntervalTicks) {
        super(null); // Animated patterns don't have a single static list of slots
        this.frames = frames;
        this.updateInterval = updateIntervalTicks;
        this.lastUpdate = TickCounter.getCurrentTick();
    }

    @Override
    public void applyPattern(AbstractGui gui, Component component) {
        if (frames == null || frames.isEmpty()) return;
        gui.setComponent(frames.get(currentFrame), component);
    }

    public List<Integer> nextFrame(AbstractGui gui, Component component) {
        if (frames == null || frames.isEmpty() || frames.size() <= 1) return List.of();

        long currentTime = TickCounter.getCurrentTick();
        if (currentTime - lastUpdate < updateInterval) {
            return List.of();
        }
        lastUpdate = currentTime;

        List<Integer> previousFrameSlots = frames.get(currentFrame);

        currentFrame = (currentFrame + 1) % frames.size();

        applyPattern(gui, component);
        return previousFrameSlots;
    }
}
