package dev.iseal.sealLib.Systems.Gui.inventory.impl;

import dev.iseal.sealLib.Systems.Gui.components.abstr.Component;
import dev.iseal.sealLib.Systems.Gui.components.impl.ButtonComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PagedGui extends ChestGui {

    private final List<Component> pageItems = new ArrayList<>();
    private final int contentStartSlot;
    private final int contentEndSlot;
    private int currentPage = 0;

    /**
     * Creates a new paged GUI with navigation buttons at the bottom row.
     * @param rows The number of rows in the GUI (must be at least 2)
     * @param title The title of the GUI
     */
    public PagedGui(int rows, String title) {
        super(rows, title);
        if (rows < 2) throw new IllegalArgumentException("PagedGui must have at least 2 rows");

        this.contentStartSlot = 0;
        this.contentEndSlot = (rows - 1) * 9 - 1;

        setupNavigationButtons(rows);
    }

    /**
     * Creates a new paged GUI with custom content area.
     * @param rows The number of rows in the GUI
     * @param title The title of the GUI
     * @param contentStartSlot The first slot of the content area
     * @param contentEndSlot The last slot of the content area
     */
    public PagedGui(int rows, String title, int contentStartSlot, int contentEndSlot) {
        super(rows, title);
        this.contentStartSlot = contentStartSlot;
        this.contentEndSlot = contentEndSlot;
    }

    private void setupNavigationButtons(int rows) {
        int lastRow = (rows - 1) * 9;

        // Previous page button
        ItemStack prevButton = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prevButton.getItemMeta();
        if (prevMeta != null) {
            prevMeta.setDisplayName("§cPrevious Page");
            prevButton.setItemMeta(prevMeta);
        }

        // Next page button
        ItemStack nextButton = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextButton.getItemMeta();
        if (nextMeta != null) {
            nextMeta.setDisplayName("§aNext Page");
            nextButton.setItemMeta(nextMeta);
        }

        // Set buttons
        setComponent(lastRow + 3, new ButtonComponent(prevButton, (player, clickType) -> {
            if (currentPage > 0) {
                currentPage--;
                updatePage();
                return true;
            }
            return false;
        }));

        setComponent(lastRow + 5, new ButtonComponent(nextButton, (player, clickType) -> {
            int itemsPerPage = contentEndSlot - contentStartSlot + 1;
            int maxPage = (int) Math.ceil((double) pageItems.size() / itemsPerPage) - 1;
            if (currentPage < maxPage) {
                currentPage++;
                updatePage();
                return true;
            }
            return false;
        }));
    }

    /**
     * Adds an item to the paged GUI.
     * @param component The component to add
     */
    public void addPageItem(Component component) {
        pageItems.add(component);
    }

    /**
     * Updates the current page.
     */
    public void updatePage() {
        // Clear content area
        for (int i = contentStartSlot; i <= contentEndSlot; i++) {
            removeComponent(i);
        }

        // Calculate items for current page
        int itemsPerPage = contentEndSlot - contentStartSlot + 1;
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, pageItems.size());

        // Add items to content area
        int slot = contentStartSlot;
        for (int i = startIndex; i < endIndex; i++) {
            setComponent(slot++, pageItems.get(i));
        }

        update();
    }

    @Override
    public void open(Player player) {
        updatePage();
        super.open(player);
    }
}
