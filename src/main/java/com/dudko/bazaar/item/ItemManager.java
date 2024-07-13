package com.dudko.bazaar.item;

import com.dudko.bazaar.Bazaar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Create a simple item with a name and lore
     *
     * @param material        material of the item
     * @param localisationKey key of the item in the lang file. <br>Create string localisation for <b>xxx.name</b> and a string list localisation for <b>xxx.lore</b>
     * @return the item created
     */
    @NotNull
    public static ItemStack simpleFormattedItem(Material material, String localisationKey) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        meta.itemName(mm.deserialize(Bazaar.getPlugin().translatedString(localisationKey + ".name")));
        List<String> loreStrings = Bazaar.getPlugin().translatedStringList(localisationKey + ".lore");
        if (loreStrings.toArray().length > 1 || !loreStrings.getFirst().isEmpty()) {
            List<Component> lore = new ArrayList<>(loreStrings.stream().map(mm::deserialize).toList());
            meta.lore(lore);
        }
        item.setItemMeta(meta);

        return item;
    }
}
