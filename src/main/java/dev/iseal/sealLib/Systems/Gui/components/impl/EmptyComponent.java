package dev.iseal.sealLib.Systems.Gui.components.impl;

import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EmptyComponent extends Component {

    public EmptyComponent() {
        super(new ItemStack(Material.AIR), UUID.randomUUID());
    }

    @Override
    public ItemStack render() {
        return getItem();
    }
}
