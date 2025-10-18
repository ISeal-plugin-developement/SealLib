package dev.iseal.sealLib.Systems.Gui.components.abstr;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PROTECTED)
@Getter
public abstract class Component {

    UUID id;

    protected Component(UUID id) {
        this.id = id;
    }

    public abstract ItemStack render();
}
