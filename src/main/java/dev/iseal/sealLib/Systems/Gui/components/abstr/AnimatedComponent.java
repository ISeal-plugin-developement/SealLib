package dev.iseal.sealLib.Systems.Gui.components.abstr;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public abstract class AnimatedComponent extends Component {

    @Getter
    private final List<ItemStack> frames;
    private int currentFrame = 0;
    private long lastUpdate;
    private final long updateInterval;

    protected AnimatedComponent(List<ItemStack> frames, UUID id, long updateIntervalMs) {
        super(id);
        if (frames == null || frames.isEmpty()) {
            throw new IllegalArgumentException("Frames cannot be null or empty.");
        }
        this.frames = frames;
        this.updateInterval = updateIntervalMs;
        this.lastUpdate = System.currentTimeMillis();
    }

    @Override
    public ItemStack render() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate >= updateInterval) {
            currentFrame = (currentFrame + 1) % frames.size();
            lastUpdate = currentTime;
        }
        return frames.get(currentFrame);
    }
}
