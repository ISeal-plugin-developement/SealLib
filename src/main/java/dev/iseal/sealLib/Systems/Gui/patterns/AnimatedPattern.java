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
        this(frames, updateIntervalTicks, 0);
    }

    public AnimatedPattern(List<List<Integer>> frames, int updateIntervalTicks, int borderOffset) {
        super(null, borderOffset); // Animated patterns don't have a single static list of slots
        this.frames = frames;
        this.updateInterval = updateIntervalTicks;
        this.lastUpdate = TickCounter.getCurrentTick();
    }

    @Override
    public void applyPattern(AbstractGui gui, Component component) {
        if (frames == null || frames.isEmpty()) return;
        gui.setComponent(frames.get(currentFrame), component);
    }

    public void applyPattern(AbstractGui gui, List<Component> components) {
        if (frames == null || frames.isEmpty() || components == null || components.isEmpty()) return;
        for (int i = 0; i < frames.size(); i++) {
            if (i < components.size() && components.get(i) != null) {
                gui.setComponent(frames.get(i), components.get(i));
            }
        }
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

        // If the pattern was registered with a null component it likely manages its
        // own slots/components (e.g. marching ants). In that case call the pattern's
        // apply(...) so the subclass can set both component sets correctly.
        if (component == null) {
            apply(gui, null); // subclass should override apply(AbstractGui, Component)
        } else {
            applyPattern(gui, component);
        }
        return previousFrameSlots;
    }
}
