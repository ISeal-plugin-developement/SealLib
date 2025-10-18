package dev.iseal.sealLib.Systems.Gui.inventory;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

public class InventoryManager {

    public static final InventoryManager INSTANCE = new InventoryManager();

    private final HashMap<Player, AbstractGui> openGuis = new HashMap<>();

    public void addOpenInventory(Player player, AbstractGui gui) {
        openGuis.put(player, gui);
    }

    public Optional<AbstractGui> getOpenInventory(Player player) {
        return Optional.ofNullable(openGuis.get(player));
    }

    public void removeOpenInventory(Player player) {
        openGuis.remove(player);
    }
}
