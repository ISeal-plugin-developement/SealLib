package dev.iseal.sealLib.Systems.Gui.patterns;

import dev.iseal.sealLib.SealLib;
import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import dev.iseal.sealLib.Systems.Gui.inventory.AbstractGui;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatternApplier {

    private final AbstractGui gui;
    private final Map<AnimatedPattern, Component> animatedPatterns = new HashMap<>();
    private BukkitTask task = null;

    public PatternApplier(AbstractGui gui) {
        this.gui = gui;
    }

    public void addPattern(AnimatedPattern pattern, Component component) {
        animatedPatterns.put(pattern, component);
        if (component != null) {
            pattern.applyPattern(gui, component);
        } else {
            // for patterns that manage their own components
            // also, this assumes that if component is null, the pattern handles its own components. if it doesn't, fuck you.
            pattern.apply(gui, null);
        }
        ensureTaskRunning();
    }

    public void updatePatterns() {
        if (animatedPatterns.isEmpty()) return;

        animatedPatterns.forEach((pattern, component) -> {
            List<Integer> previousFrameSlots = pattern.nextFrame(gui, component);

            // If component is null the pattern manages its own components/clearing,
            // so don't attempt to clear slots here.
            if (component == null) {
                return;
            }

            for (Integer slot : previousFrameSlots) {
                // only clear the slot if the component hasn't been replaced by something else
                if (gui.getComponent(slot).map(c -> c.equals(component)).orElse(false)) {
                    gui.removeComponent(slot);
                }
            }
        });
    }

    private void ensureTaskRunning() {
        if (task != null && !task.isCancelled()) return;

        // schedule a sync repeating task that updates the gui each tick
        task = Bukkit.getScheduler().runTaskTimer(SealLib.getPlugin(), () -> {
            try {
                gui.update();
            } finally {
                if (animatedPatterns.isEmpty()) {
                    stop();
                }
            }
        }, 1L, 1L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
