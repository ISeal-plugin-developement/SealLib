package dev.iseal.sealLib.Systems.Gui.inventory.impl;

import dev.iseal.sealLib.Systems.Gui.inventory.AbstractGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChestGui extends AbstractGui {

    public ChestGui(int rows, String title) {
        this(rows, title, false);
    }

    public ChestGui(int rows, String title, boolean allowMovement) {
        super(Bukkit.createInventory(null, rows * 9, title), 9, rows, allowMovement);
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("Chest GUI rows must be between 1 and 6.");
        }
    }

    @Override
    public void open(Player player) {
        super.open(player);
    }
}
