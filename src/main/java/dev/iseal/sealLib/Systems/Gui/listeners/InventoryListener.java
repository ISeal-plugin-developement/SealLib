package dev.iseal.sealLib.Systems.Gui.listeners;

import dev.iseal.sealLib.Systems.Gui.inventory.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        InventoryManager.INSTANCE.getOpenInventory(player).ifPresent(gui -> {
            if (event.getClickedInventory() == gui.getInventory()) {
                event.setCancelled(!gui.isAllowMovement());

                // Let the GUI handle the click
                boolean handled = gui.handleClick(player, event.getSlot(), event.getClick());
                if (handled) {
                    event.setCancelled(true);
                }
            }
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            InventoryManager.INSTANCE.removeOpenInventory(player);
        }
    }
}
