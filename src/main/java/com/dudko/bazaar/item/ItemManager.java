package com.dudko.bazaar.item;

import com.dudko.bazaar.Bazaar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ItemManager {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private final Bazaar plugin = Bazaar.getPlugin();

    public static ItemStack MARKET;
    public static ItemStack ADMIN_MARKET;

    public void registerItems() {
        MARKET = createGlassDisplayShop();
        ADMIN_MARKET = createGlassDisplayAdminShop();
    }

    private ItemStack createGlassDisplayShop() {
        ItemStack item = new ItemStack(Material.GLASS);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.itemName(mm.deserialize("<!i><yellow>Market</yellow>"));
        meta.setEnchantmentGlintOverride(true);
        meta.setMaxStackSize(1);

        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "item_id"), PersistentDataType.STRING, "market_item");

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

        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "item_id"), PersistentDataType.STRING, "admin_market_item");

        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    public static Component itemNameOrDisplayName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return Component.empty();
        }
        Component name;
        if (meta.hasDisplayName()) {
            name = meta.displayName();
        }
        else if (meta.hasItemName()) {
            name = meta.itemName();
        }
        else {
            name = Component.translatable(item.translationKey());
        }
        assert name != null;
        return name;
    }
}
