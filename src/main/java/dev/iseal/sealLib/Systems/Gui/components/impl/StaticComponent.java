package dev.iseal.sealLib.Systems.Gui.components.impl;

import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class StaticComponent extends Component {

    public StaticComponent(ItemStack stack) {
        super(stack, UUID.randomUUID());
    }

    @Override
    public ItemStack render() {
        return getItem();
    }
}
