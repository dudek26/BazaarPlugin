package com.dudko.bazaar.gui;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.gui.items.CloseItem;
import com.dudko.bazaar.gui.items.MarketNavigationItem;
import com.dudko.bazaar.item.ItemManager;
import com.dudko.bazaar.market.Market;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.window.Window;

public class MarketMainGUI extends MarketGUI {

    public MarketMainGUI(Market market, Player player) {
        super(market, player);
    }

    @Override
    public void display() {

        MarketNavigationItem navigateToItems = new MarketNavigationItem(new MarketItemsGUI(getMarket(), getViewer()),
                                                                        ItemManager.simpleFormattedItem(Material.BOOK,
                                                                                                        "gui.market.browse-item"));
        MarketNavigationItem navigateToManagement = new MarketNavigationItem(new MarketManageGUI(getMarket(),
                                                                                                 getViewer()),
                                                                             ItemManager.simpleFormattedItem(Material.GOLD_INGOT,
                                                                                                             "gui.market.manage-item"));

        Structure playerStructure = new Structure("# # # # # # # # #", "# # # b # c # # #", "# # # # x # # # #");
        Structure adminStructure = new Structure("# # # # # # # # #", "# # # b # c # # #", "# # # # x # a # #");
        Structure structure = getMarket().isOwner(getViewer())
                              || getMarket().isCoOwner(getViewer())
                              || getViewer().hasPermission("bazaar.admin") ? adminStructure : playerStructure;

        Gui gui = Gui
                .normal()
                .setStructure(structure)
                .addIngredient('b', navigateToItems)
                .addIngredient('x', new CloseItem(Material.BARRIER))
                .addIngredient('c', navigateToManagement)
                .build();


        Window window = Window.single().setViewer(getViewer()).setTitle(getMarket().getName()).setGui(gui).build();
        window.open();

    }

    public static class MarketPlayerItemsNav extends AbstractItem {

        private final Market market;
        private static final MiniMessage mm = MiniMessage.miniMessage();
        private final Bazaar plugin = Bazaar.getPlugin();

        public MarketPlayerItemsNav(Market market) {
            this.market = market;
        }

        @Override
        public ItemProvider getItemProvider() {
            ItemStack item = ItemManager.simpleFormattedItem(Material.GOLD_BLOCK, "gui.market.player-items-item");
            return new ItemBuilder(item);
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {

        }
    }

}
