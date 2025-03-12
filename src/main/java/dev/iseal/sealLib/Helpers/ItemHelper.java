package dev.iseal.sealLib.Helpers;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemHelper {

    public static ItemStack addGlow(ItemStack itemStack) {
        // adds protection to bows and infinity to every other item as infinity is only useful on bows and protection is only useful on armor
        itemStack.addUnsafeEnchantment((itemStack.getType() == Material.BOW) ? Enchantment.PROTECTION_ENVIRONMENTAL : Enchantment.ARROW_INFINITE, 1);
        // hides the enchantments
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
        // returns the new itemstack
        return itemStack;
    }

}
