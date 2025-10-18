package dev.iseal.sealLib.Systems.Gui.components.impl;

import dev.iseal.sealLib.Systems.Gui.components.abstr.ClickableComponent;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.BiFunction;

public class ButtonComponent extends ClickableComponent {

    @Setter
    private BiFunction<Player, ClickType, Boolean> clickHandler;

    public ButtonComponent(ItemStack item) {
        super(item, UUID.randomUUID());
    }

    public ButtonComponent(ItemStack item, BiFunction<Player, ClickType, Boolean> clickHandler) {
        super(item, UUID.randomUUID());
        this.clickHandler = clickHandler;
    }

    @Override
    public boolean onClick(Player player, ClickType clickType) {
        return clickHandler != null && clickHandler.apply(player, clickType);
    }
}

