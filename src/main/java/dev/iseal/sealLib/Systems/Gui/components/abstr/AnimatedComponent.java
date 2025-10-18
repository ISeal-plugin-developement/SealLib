package dev.iseal.sealLib.Systems.Gui.components.abstr;

import dev.iseal.sealLib.Utils.TickCounter;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public abstract class AnimatedComponent extends Component {

    @Getter
    private final List<ItemStack> frames;
    private int currentFrame = 0;
    private long lastUpdate;
    private final long updateIntervalTicks;

    protected AnimatedComponent(List<ItemStack> frames, UUID id, int updateIntervalTicks) {
        super((frames == null || frames.isEmpty()) ? null : frames.get(0), id);
        if (frames == null || frames.isEmpty()) {
            throw new IllegalArgumentException("Frames cannot be null or empty.");
        }
        this.frames = frames;
        this.updateIntervalTicks = updateIntervalTicks;
        this.lastUpdate = TickCounter.getCurrentTick();
    }

    @Override
    public ItemStack render() {
        long currentTick = TickCounter.getCurrentTick();
        if (currentTick - lastUpdate >= updateIntervalTicks) {
            currentFrame = (currentFrame + 1) % frames.size();
            lastUpdate = currentTick;
        }
        return frames.get(currentFrame);
    }
}
