package com.dudko.bazaar.gui;

import com.dudko.bazaar.gui.items.BackItem;
import com.dudko.bazaar.gui.items.ForwardItem;
import com.dudko.bazaar.gui.items.MarketNavigationItem;
import com.dudko.bazaar.item.ItemManager;
import com.dudko.bazaar.market.Market;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.stream.Collectors;

public class MarketManageGUI extends MarketGUI {

    public MarketManageGUI(Market market, Player player) {
        super(market, player);
    }

    @Override
    public void display() {

        List<Item> items = getMarket()
                .getMarketItems()
                .stream()
                .filter(marketItem -> marketItem.getSeller().getUniqueId().equals(getViewer().getUniqueId()))
                .map(marketItem -> new MarketItemsGUI.GUIItem(marketItem, getMarket()))
                .collect(Collectors.toList());

        Gui gui = PagedGui
                .items()
                .setStructure("x x x x x x x x x", "x x x x x x x x x", "# < # # c # a > #")
                .setContent(items)
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('>', new ForwardItem())
                .addIngredient('<', new BackItem())
                .addIngredient('c',
                               new MarketNavigationItem(new MarketMainGUI(getMarket(), getViewer()),
                                                        ItemManager.simpleFormattedItem(Material.ARROW,
                                                                                        "gui.market.back-to-main-menu-item")))
                .build();

        Window window = Window.single().setViewer(getViewer()).setTitle(getMarket().getName()).setGui(gui).build();
        window.open();
    }


}
