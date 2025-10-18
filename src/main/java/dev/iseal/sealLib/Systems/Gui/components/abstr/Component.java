package dev.iseal.sealLib.Systems.Gui.components.abstr;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@FieldDefaults(level = lombok.AccessLevel.PROTECTED)
@Getter
public abstract class Component {

    UUID id;
    ItemStack item;

    protected Component(ItemStack item, UUID id) {
        this.item = item;
        this.id = id;
    }

    public abstract ItemStack render();
}
