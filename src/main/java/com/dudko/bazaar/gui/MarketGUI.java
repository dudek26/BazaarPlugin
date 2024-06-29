package com.dudko.bazaar.gui;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.market.Market;
import com.dudko.bazaar.market.MarketItem;
import com.dudko.bazaar.util.SimpleTime;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MarketGUI {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private final Bazaar plugin = Bazaar.getPlugin();

    public void display(Player player, Market market) {
        List<Item> items = market.getMarketItems().stream().map(this::marketItem).toList();

        Gui gui = PagedGui
                .items()
                .setStructure("x x x x x x x x x",
                              "x x x x x x x x x",
                              "x x x x x x x x x",
                              "x x x x x x x x x",
                              "x x x x x x x x x",
                              "# < # # x s # > #")
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('<', new GlobalItems.BackItem())
                .addIngredient('>', new GlobalItems.ForwardItem())
                .setContent(items)
                .build();

        Window window = Window.single().setViewer(player).setTitle(market.getName()).setGui(gui).build();

        window.open();
    }

    public Item marketItem(MarketItem marketItem) {
        ItemStack item = marketItem.getItemStack().clone();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        List<Component> lore;
        if (meta.hasLore()) lore = new ArrayList<>(Objects.requireNonNull(meta.lore()));
        else {
            lore = new ArrayList<>();
            lore.add(Component.empty());
        }

        if (lore.getLast() != Component.empty()) {
            lore.add(Component.empty());
        }
        lore.addAll(plugin
                            .translatedStringList("gui.market.item.lore")
                            .stream()
                            .map(s -> parsedComponent(s, marketItem))
                            .toList());
        if (marketItem.isInfinite()) {
            lore.add(Component.empty());
            lore.add(mm.deserialize(plugin.translatedString("gui.market.item.infinite")));
        }
        meta.lore(lore);
        item.setItemMeta(meta);
        return new SimpleItem(item);
    }

    private Component parsedComponent(String string, MarketItem marketItem) {

        long second = Instant.now().getEpochSecond() - marketItem.getCreationDate();
        String time = new SimpleTime(second).parse();

        String tax = marketItem.isTaxIncluded() ?
                     plugin
                             .translatedString("gui.market.item.tax")
                             .replace("<tax_percentage>", marketItem.getTaxAmount() * 100 + "%")
                             .replace("<tax_amount>",
                                      Double.toString(marketItem.getTaxAmount() * marketItem.getPrice())) :
                     "gui.market.item.no-tax";

        return mm.deserialize(string,
                              Placeholder.unparsed("price", Double.toString(marketItem.getPrice())),
                              Placeholder.unparsed("seller", Objects.requireNonNull(marketItem.getSeller().getName())),
                              Placeholder.parsed("age", time),
                              Placeholder.parsed("tax", tax));
    }

}
