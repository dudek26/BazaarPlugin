package com.dudko.bazaar.item;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static ItemStack GLASS_DISPLAY_SHOP;
    public static ItemStack GLASS_DISPLAY_ADMIN_SHOP;

    public void registerItems() {
        GLASS_DISPLAY_SHOP = createGlassDisplayShop();
        GLASS_DISPLAY_ADMIN_SHOP = createGlassDisplayAdminShop();
    }

    private ItemStack createGlassDisplayShop() {
        ItemStack item = new ItemStack(Material.GLASS);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.itemName(mm.deserialize("<!i><yellow>Market</yellow>"));
        meta.setEnchantmentGlintOverride(true);
        meta.setMaxStackSize(1);

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createGlassDisplayAdminShop() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.itemName(mm.deserialize("<!i><red>Admin Market</red>"));
        meta.setEnchantmentGlintOverride(true);
        meta.setMaxStackSize(1);

        item.setItemMeta(meta);
        return item;
    }

}
