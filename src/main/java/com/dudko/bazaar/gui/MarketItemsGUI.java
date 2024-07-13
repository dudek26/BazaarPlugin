package com.dudko.bazaar.gui;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.gui.items.BackItem;
import com.dudko.bazaar.gui.items.ForwardItem;
import com.dudko.bazaar.gui.items.MarketNavigationItem;
import com.dudko.bazaar.item.ItemManager;
import com.dudko.bazaar.market.Market;
import com.dudko.bazaar.market.MarketItem;
import com.dudko.bazaar.util.SimpleTime;
import com.dudko.bazaar.util.Sounds;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.window.Window;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class MarketItemsGUI extends MarketGUI {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public MarketItemsGUI(Market market, Player viewer) {
        super(market, viewer);
    }

    @Override
    public void display() {
        List<Item> items = getMarket()
                .getMarketItems()
                .stream()
                .map(marketItem -> new GUIItem(marketItem, getMarket()))
                .collect(Collectors.toList());

        Gui gui = PagedGui
                .items()
                .setStructure("x x x x x x x x x",
                              "x x x x x x x x x",
                              "x x x x x x x x x",
                              "x x x x x x x x x",
                              "x x x x x x x x x",
                              "# < # s c f o > #")
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('<', new BackItem())
                .addIngredient('>', new ForwardItem())
                .addIngredient('c',
                               new MarketNavigationItem(new MarketMainGUI(getMarket(), getViewer()),
                                                        ItemManager.simpleFormattedItem(Material.ARROW,
                                                                                        "gui.market.back-to-main-menu-item")))
                // o - market settings (for market admins)
                // f - filter & sort
                // s - search
                .setContent(items)
                .build();

        Window window = Window.single().setViewer(getViewer()).setTitle(getMarket().getName()).setGui(gui).build();

        window.open();
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static class GUIItem extends AbstractItem {

        private final MarketItem marketItem;
        private final Market market;
        private final Bazaar plugin = Bazaar.getPlugin();
        private final Economy economy = Bazaar.getEconomy();

        public GUIItem(@NotNull MarketItem marketItem, Market market) {
            this.marketItem = marketItem;
            this.market = market;
        }

        @Override
        public ItemProvider getItemProvider() {
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
            return new ItemBuilder(item);
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            try {
                if (plugin.getDatabase().marketItemExists(marketItem.getId())) {
                    new MarketItemsGUI.BuyMenu(market, marketItem, player).display();
                }
                else {
                    Sounds.ERROR.play(player);
                    player.sendMessage(mm.deserialize(plugin.translatedString("gui.market.message.item-unavailable")));

                }
            } catch (SQLException e) {
                e.printStackTrace();
                Sounds.ERROR.play(player);
                player.sendMessage(mm.deserialize(plugin.translatedString("gui.market.message.error"),
                                                  Placeholder.unparsed("error",
                                                                       MarketItem.BuyResult.SQL_EXCEPTION.name())));

            }
        }


        private Component parsedComponent(String string, MarketItem marketItem) {

            long second = Instant.now().getEpochSecond() - marketItem.getCreationDate();
            String time = new SimpleTime(second).parse();

            String tax = marketItem.isTaxIncluded() ?
                         plugin
                                 .translatedString("gui.market.item.tax")
                                 .replace("<tax_percentage>", marketItem.getTaxAmount() * 100 + "%")
                                 .replace("<tax_amount>",
                                          Double.toString(marketItem.getTaxAmount() * marketItem.getPrice()))
                                 .replace("<currency>", economy.currencyNamePlural()) :
                         "gui.market.item.no-tax";

            return mm.deserialize(string,
                                  Placeholder.unparsed("price", Double.toString(marketItem.getPrice())),
                                  Placeholder.unparsed("seller",
                                                       Objects.requireNonNull(marketItem.getSeller().getName())),
                                  Placeholder.parsed("age", time),
                                  Placeholder.parsed("tax", tax),
                                  Placeholder.unparsed("currency", economy.currencyNamePlural()));
        }

    }

    public static class BuyMenu {

        private final Market market;
        private final MarketItem item;
        private final Player player;
        private final Bazaar plugin = Bazaar.getPlugin();

        public BuyMenu(Market market, MarketItem item, Player player) {
            this.market = market;
            this.item = item;
            this.player = player;
        }

        public void display() {
            Gui gui = Gui
                    .normal()
                    .setStructure("# # # # # # # # #",
                                  "# # # # i # # # #",
                                  "# # # # # # # # #",
                                  "# # # # b # # # #",
                                  "# # # # # # # # #",
                                  "# # # # x # # # #")
                    .addIngredient('i', item.getItemStack())
                    .addIngredient('b', new BuyItem(item))
                    .addIngredient('x',
                                   new MarketNavigationItem(new MarketItemsGUI(market, player),
                                                            ItemManager.simpleFormattedItem(Material.ARROW,
                                                                                            "gui.market.back-to-browsing-item")))
                    .build();

            Window window = Window
                    .single()
                    .setViewer(player)
                    .setTitle(plugin.translatedString("gui.market.buy-title"))
                    .setGui(gui)
                    .build();
            window.open();
        }


        @SuppressWarnings("CallToPrintStackTrace")
        public class BuyItem extends AbstractItem {

            private final Bazaar plugin = Bazaar.getPlugin();
            private final MarketItem marketItem;
            private final Economy economy = Bazaar.getEconomy();

            public BuyItem(MarketItem marketItem) {
                this.marketItem = marketItem;
            }

            @Override
            public ItemProvider getItemProvider() {
                ItemStack item = new ItemStack(Material.GOLD_NUGGET);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.itemName(parsedComponent(plugin.translatedString("gui.market.buy-item.name"), marketItem));
                List<Component> lore = new ArrayList<>(plugin
                                                               .translatedStringList("gui.market.buy-item.lore")
                                                               .stream()
                                                               .map(l -> parsedComponent(l, marketItem))
                                                               .toList());
                meta.lore(lore);

                item.setItemMeta(meta);
                return new ItemBuilder(item);
            }

            private Component parsedComponent(String string, MarketItem marketItem) {

                long second = Instant.now().getEpochSecond() - marketItem.getCreationDate();
                String time = new SimpleTime(second).parse();

                String tax = marketItem.isTaxIncluded() ?
                             plugin
                                     .translatedString("gui.market.buy-item.tax")
                                     .replace("<tax_percentage>", marketItem.getTaxAmount() * 100 + "%")
                                     .replace("<tax_amount>",
                                              Double.toString(marketItem.getTaxAmount() * marketItem.getPrice()))
                                     .replace("<currency>", economy.currencyNamePlural()) :
                             "gui.market.buy-item.no-tax";

                return mm.deserialize(string,
                                      Placeholder.unparsed("price", Double.toString(marketItem.getPrice())),
                                      Placeholder.unparsed("seller",
                                                           Objects.requireNonNull(marketItem.getSeller().getName())),
                                      Placeholder.parsed("age", time),
                                      Placeholder.parsed("tax", tax),
                                      Placeholder.unparsed("currency", economy.currencyNamePlural()));
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                try {
                    if (plugin.getDatabase().marketItemExists(marketItem.getId())) {
                        double balance = economy.getBalance(player, player.getWorld().getName());
                        if (balance >= marketItem.getPrice()) {
                            new ConfirmMenu(market, marketItem, player).display();
                        }
                        else {
                            player.sendMessage(mm.deserialize(plugin.translatedString(
                                                                      "gui.market.message.not-enough-money"),
                                                              Placeholder.unparsed("currency",
                                                                                   economy.currencyNamePlural()),
                                                              Placeholder.unparsed("price",
                                                                                   Double.toString(marketItem.getPrice()))));
                        }
                    }
                    else {
                        Sounds.ERROR.play(player);
                        player.sendMessage(mm.deserialize(plugin.translatedString("gui.market.message.item-unavailable")));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Sounds.ERROR.play(player);
                    player.sendMessage(mm.deserialize(plugin.translatedString("gui.market.message.error"),
                                                      Placeholder.unparsed("error",
                                                                           MarketItem.BuyResult.SQL_EXCEPTION.name())));
                }
            }
        }

    }

    public static class ConfirmMenu {

        private final Bazaar plugin = Bazaar.getPlugin();
        private final Market market;
        private final MarketItem item;
        private final Player player;

        public ConfirmMenu(Market market, MarketItem item, Player player) {
            this.market = market;
            this.item = item;
            this.player = player;
        }

        public void display() {
            Gui gui = Gui
                    .normal()
                    .setStructure("# # # # # # # # #", "# # y # i # n # #", "# # # # # # # # #")
                    .addIngredient('i', boughtItem())
                    .addIngredient('y', new ConfirmItem(item, player))
                    .addIngredient('n',
                                   new MarketNavigationItem(new MarketItemsGUI(market, player),
                                                            ItemManager.simpleFormattedItem(Material.RED_CONCRETE,
                                                                                            "gui.market.confirmation.cancel-item")))
                    .build();

            Window window = Window
                    .single()
                    .setViewer(player)
                    .setTitle(plugin.translatedString("gui.market.confirmation.title"))
                    .setGui(gui)
                    .build();
            window.open();
        }

        private ItemStack boughtItem() {
            ItemStack item = this.item.getItemStack().clone();
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            Component name = ItemManager.itemNameOrDisplayName(item);
            meta.itemName(mm.deserialize(plugin.translatedString("gui.market.confirmation.item-name"),
                                         Placeholder.component("item_name", name)));
            item.setItemMeta(meta);
            return item;
        }

        @SuppressWarnings("CallToPrintStackTrace")
        public static class ConfirmItem extends AbstractItem {

            private final Bazaar plugin = Bazaar.getPlugin();
            private final Economy econ = Bazaar.getEconomy();
            private final MarketItem marketItem;
            private final Player player;

            public ConfirmItem(MarketItem marketItem, Player player) {
                this.marketItem = marketItem;
                this.player = player;
            }

            @Override
            public ItemProvider getItemProvider() {
                ItemStack item = new ItemStack(Material.GREEN_CONCRETE);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.itemName(parsedComponent(plugin.translatedString("gui.market.confirmation.confirm-item.name")));
                List<String> loreStrings = plugin.translatedStringList("gui.market.confirmation.confirm-item.lore");
                if (loreStrings.toArray().length > 1 || !loreStrings.getFirst().isEmpty()) {
                    List<Component> lore = new ArrayList<>(loreStrings.stream().map(this::parsedComponent).toList());
                    meta.lore(lore);
                }

                item.setItemMeta(meta);
                return new ItemBuilder(item);
            }

            @Override
            public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                MarketItem.BuyResult result = marketItem.buy(player);
                switch (result) {
                    case SUCCESS -> {
                        Sounds.BUY.play(player);
                        player.sendMessage(boughtMessage(plugin.translatedString("gui.market.message.item-bought")));
                        MarketGUI.safeCloseInventory(player);
                    }
                    case NO_INVENTORY_SPACE -> {
                        Sounds.ERROR.play(player);
                        player.sendMessage(mm.deserialize(plugin.translatedString(
                                "gui.market.message.no-inventory-space")));
                    }
                    case SQL_EXCEPTION, ITEM_DOES_NOT_EXIST, MARKET_DOES_NOT_EXIST -> {
                        Sounds.ERROR.play(player);
                        player.sendMessage(mm.deserialize(plugin.translatedString("gui.market.message.buy-error"),
                                                          Placeholder.unparsed("error", result.name())));
                        MarketGUI.safeCloseInventory(player);
                    }
                    case NOT_ENOUGH_MONEY -> {
                        Sounds.ERROR.play(player);
                        player.sendMessage(mm.deserialize(plugin.translatedString("gui.market.message.not-enough-money"),
                                                          Placeholder.unparsed("currency", econ.currencyNamePlural()),
                                                          Placeholder.unparsed("price",
                                                                               Double.toString(marketItem.getPrice()))));
                    }
                }
            }

            private Component boughtMessage(String string) {
                return mm.deserialize(string,
                                      Placeholder.component("item_name",
                                                            ItemManager.itemNameOrDisplayName(marketItem.getItemStack())),
                                      Placeholder.component("item", marketItem.getItemStack().displayName()),
                                      Placeholder.unparsed("currency", econ.currencyNamePlural()),
                                      Placeholder.unparsed("price", Double.toString(marketItem.getPrice())),
                                      Placeholder.unparsed("seller",
                                                           Objects.requireNonNull(marketItem.getSeller().getName())));
            }

            private Component parsedComponent(String string) {

                Component name = ItemManager.itemNameOrDisplayName(marketItem.getItemStack());

                return mm.deserialize(string,
                                      Placeholder.component("item_name", name),
                                      Placeholder.unparsed("currency", econ.currencyNamePlural()),
                                      Placeholder.unparsed("price", Double.toString(marketItem.getPrice())),
                                      Placeholder.unparsed("seller",
                                                           Objects.requireNonNull(marketItem.getSeller().getName())),
                                      Placeholder.unparsed("balance",
                                                           Double.toString(econ.getBalance(player,
                                                                                           player
                                                                                                   .getWorld()
                                                                                                   .getName()))),
                                      Placeholder.unparsed("balance_after",
                                                           Double.toString(econ.getBalance(player,
                                                                                           player.getWorld().getName())
                                                                           - marketItem.getPrice())));
            }
        }

    }

}
