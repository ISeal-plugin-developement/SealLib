package dev.iseal.sealLib.Systems.Gui.components.abstr;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public abstract class ClickableComponent extends Component {

    protected ClickableComponent(ItemStack item, UUID id) {
        super(item, id);
    }

    /**
     * Called when the component is clicked.
     * @param player The player who clicked
     * @param clickType The type of click
     * @return true if the click was handled, false otherwise
     */
    public abstract boolean onClick(Player player, ClickType clickType);

    @Override
    public ItemStack render() {
        return getItem();
    }
}
