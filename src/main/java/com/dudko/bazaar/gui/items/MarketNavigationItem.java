package com.dudko.bazaar.gui.items;

import com.dudko.bazaar.gui.MarketGUI;
import com.dudko.bazaar.market.Market;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class MarketNavigationItem extends AbstractItem {

    private final MarketGUI gui;
    private final ItemStack itemStack;

    public MarketNavigationItem(MarketGUI gui, ItemStack itemStack) {
        this.gui = gui;
        this.itemStack = itemStack;
    }

    @Override
    public ItemProvider getItemProvider() {
        return new ItemBuilder(itemStack);
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        gui.display();
    }
}
