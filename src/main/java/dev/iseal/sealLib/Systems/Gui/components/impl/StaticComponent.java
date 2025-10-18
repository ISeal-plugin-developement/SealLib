package dev.iseal.sealLib.Systems.Gui.components.impl;

import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class StaticComponent extends Component {

    private final ItemStack item;

    public StaticComponent(ItemStack stack) {
        super(UUID.randomUUID());
        this.item = stack;
    }

    @Override
    public ItemStack render() {
        return item;
    }
}
