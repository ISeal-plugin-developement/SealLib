package dev.iseal.sealLib.Systems.Gui.inventory;

import dev.iseal.sealLib.Systems.Gui.components.abstr.ClickableComponent;
import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import dev.iseal.sealLib.Systems.Gui.patterns.AnimatedPattern;
import dev.iseal.sealLib.Systems.Gui.patterns.Pattern;
import dev.iseal.sealLib.Systems.Gui.patterns.PatternApplier;

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
    private final PatternApplier patternApplier = new PatternApplier(this);
    private final int width;
    private final int height;

    protected AbstractGui(Inventory inventory, int width, int height, boolean allowMovement) {
        this.inventory = inventory;
        this.width = width;
        this.height = height;
        this.allowMovement = allowMovement;
    }

    public void open(Player player) {
        renderComponents();
        player.openInventory(this.getInventory());
        InventoryManager.INSTANCE.addOpenInventory(player, this);
    }

    public void close(Player player) {
        // stop any pattern scheduling for this GUI to avoid leaked tasks
        patternApplier.stop();
        player.closeInventory();
        InventoryManager.INSTANCE.removeOpenInventory(player);
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
     * Applies a static pattern to the GUI.
     * @param pattern The pattern to apply.
     * @param component The component to use for the pattern.
     */
    public void applyPattern(Pattern pattern, Component component) {
        pattern.apply(this, component);
    }

    /**
     * Applies an animated pattern to the GUI.
     * @param pattern The animated pattern to apply.
     * @param component The component to use for the pattern.
     */
    public void applyPattern(AnimatedPattern pattern, Component component) {
        patternApplier.addPattern(pattern, component);
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
     * Clears an item from a specific slot in the inventory.
     * @param slot The slot to clear.
     */
    public void clearSlot(int slot) {
        inventory.setItem(slot, null);
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
        patternApplier.updatePatterns();
        for (int i = 0; i < inventory.getSize(); i++) {
            Component component = components.get(i);
            if (component != null) {
                inventory.setItem(i, component.render());
            } else {
                inventory.setItem(i, null);
            }
        }
    }

    /**
     * Updates the GUI by re-rendering all components.
     */
    public void update() {
        renderComponents();
    }
}
