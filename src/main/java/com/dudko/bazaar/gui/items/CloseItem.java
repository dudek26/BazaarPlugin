package com.dudko.bazaar.gui.items;

import com.dudko.bazaar.gui.MarketGUI;
import com.dudko.bazaar.item.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class CloseItem extends AbstractItem {

    private final Material material;

    public CloseItem(Material material) {
        this.material = material;
    }

    @Override
    public ItemProvider getItemProvider() {
        ItemStack item = ItemManager.simpleFormattedItem(material, "gui.close-item");
        return new ItemBuilder(item);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        MarketGUI.safeCloseInventory(player);
    }
}
