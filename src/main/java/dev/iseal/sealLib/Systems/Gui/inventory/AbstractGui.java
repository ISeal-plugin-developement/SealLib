package dev.iseal.sealLib.Systems.Gui.inventory;

import dev.iseal.sealLib.Systems.Gui.components.abstr.ClickableComponent;
import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public abstract class AbstractGui {

    private final Inventory inventory;
    private final boolean allowMovement;
    private final Map<Integer, Component> components = new HashMap<>();
    private final int width;
    private final int height;

    protected AbstractGui(Inventory inventory, int width, int height, boolean allowMovement) {
        this.inventory = inventory;
        this.width = width;
        this.height = height;
        this.allowMovement = allowMovement;
    }

    /**
     * Opens the GUI for the player. Remember to override this method in your implementation.
     * @param player The player to open the GUI for.
     */
    public void open(Player player) {
        renderComponents();
        player.openInventory(this.getInventory());
        InventoryManager.INSTANCE.addOpenInventory(player, this);
    }

    /**
     * Sets a component at a specific slot in the GUI.
     * @param slot The slot to set the component at.
     * @param component The component to set.
     */
    public void setComponent(int slot, Component component) {
        components.put(slot, component);
    }

    /**
     * Sets a component at a specific coordinate in the GUI.
     * @param x The x-coordinate (column, 0-indexed).
     * @param y The y-coordinate (row, 0-indexed).
     * @param component The component to set.
     */
    public void setComponent(int x, int y, Component component) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Coordinates (" + x + ", " + y + ") are out of bounds for a " + width + "x" + height + " GUI.");
        }
        setComponent(y * width + x, component);
    }

    /**
     * Sets a component at multiple slots in the GUI.
     * @param slots The collection of slots to set the component at.
     * @param component The component to set.
     */
    public void setComponent(Collection<Integer> slots, Component component) {
        for (Integer slot : slots) {
            setComponent(slot, component);
        }
    }

    /**
     * Removes a component from a specific slot in the GUI.
     * @param slot The slot to remove the component from.
     */
    public void removeComponent(int slot) {
        components.remove(slot);
    }

    /**
     * Removes a component from a specific coordinate in the GUI.
     * @param x The x-coordinate (column, 0-indexed).
     * @param y The y-coordinate (row, 0-indexed).
     */
    public void removeComponent(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Coordinates (" + x + ", " + y + ") are out of bounds for a " + width + "x" + height + " GUI.");
        }
        removeComponent(y * width + x);
    }

    /**
     * Gets a component from a specific slot in the GUI.
     * @param slot The slot to get the component from.
     * @return An Optional containing the component, or empty if no component exists.
     */
    public Optional<Component> getComponent(int slot) {
        return Optional.ofNullable(components.get(slot));
    }

    /**
     * Gets a component from a specific coordinate in the GUI.
     * @param x The x-coordinate (column, 0-indexed).
     * @param y The y-coordinate (row, 0-indexed).
     * @return An Optional containing the component, or empty if no component exists.
     */
    public Optional<Component> getComponent(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Coordinates (" + x + ", " + y + ") are out of bounds for a " + width + "x" + height + " GUI.");
        }
        return getComponent(y * width + x);
    }

    /**
     * Fills all empty slots with the specified component.
     * @param component The component to fill empty slots with
     */
    public void fillEmpty(Component component) {
        for (int i = 0; i < inventory.getSize(); i++) {
            components.putIfAbsent(i, component);
        }
    }

    /**
     * Handles a click in the GUI.
     * @param player The player who clicked.
     * @param slot The slot that was clicked.
     * @param clickType The type of click.
     * @return true if the click was handled, false otherwise.
     */
    public boolean handleClick(Player player, int slot, ClickType clickType) {
        return getComponent(slot)
                .filter(component -> component instanceof ClickableComponent)
                .map(component -> ((ClickableComponent) component).onClick(player, clickType))
                .orElse(false);
    }

    /**
     * Renders all components in the GUI.
     */
    public void renderComponents() {
        components.forEach((slot, component) ->
            inventory.setItem(slot, component.render())
        );
    }

    /**
     * Updates the GUI by re-rendering all components.
     */
    public void update() {
        renderComponents();
    }
}
