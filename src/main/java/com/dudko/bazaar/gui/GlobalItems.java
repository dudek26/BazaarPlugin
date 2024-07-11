package com.dudko.bazaar.gui;

import com.dudko.bazaar.Bazaar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

import java.util.ArrayList;
import java.util.List;

public class GlobalItems {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final Bazaar plugin = Bazaar.getPlugin();

    public static void init() {
        Structure.addGlobalIngredient('#', blankSlot(Material.GRAY_STAINED_GLASS_PANE));
    }

    /**
     * Safely close a player's inventory. Used in Inventory Events.
     * @param player The player to close the inventory for.
     */
    public static void safeCloseInventory(@NotNull Player player) {
        Bukkit
                .getScheduler()
                .runTask(Bazaar.getPlugin(), () -> player.closeInventory(InventoryCloseEvent.Reason.PLUGIN));
    }

    public static class CloseItem extends AbstractItem {

        private final Material material;

        public CloseItem(Material material) {
            this.material = material;
        }

        @Override
        public ItemProvider getItemProvider() {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.itemName(mm.deserialize(plugin.translatedString("gui.close-item.name")));
            List<String> loreStrings = plugin.translatedStringList("gui.close-item.lore");
            if (loreStrings.toArray().length > 1 || !loreStrings.getFirst().isEmpty()) {
                List<Component> lore = new ArrayList<>(loreStrings.stream().map(mm::deserialize).toList());
                meta.lore(lore);
            }

            item.setItemMeta(meta);
            return new ItemBuilder(item);
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            safeCloseInventory(player);
        }
    }

    public static class BackItem extends PageItem {

        public BackItem() {
            super(false);
        }

        @Override
        public ItemProvider getItemProvider(PagedGui<?> gui) {
            ItemBuilder builder;
            if (gui.hasPreviousPage()) {
                ItemStack item = new ItemStack(Material.ARROW);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.itemName(parsedName(gui.getCurrentPage(), gui.getPageAmount()));
                meta.lore(parsedLore(gui.getCurrentPage(), gui.getPageAmount()));
                item.setItemMeta(meta);
                builder = new ItemBuilder(item);
            }
            else builder = new ItemBuilder(blankSlot(Material.GRAY_STAINED_GLASS_PANE));
            return builder;
        }

        private static Component parsedName(int currentPage, int pages) {
            return mm.deserialize(plugin.translatedString("gui.previous-page-item.name"),
                                  Placeholder.unparsed("page", Integer.toString(currentPage)),
                                  Placeholder.unparsed("pages", Integer.toString(pages)));
        }

        private static List<Component> parsedLore(int currentPage, int pages) {
            return plugin
                    .translatedStringList("gui.previous-page-item.lore")
                    .stream()
                    .map(s -> mm.deserialize(s,
                                             Placeholder.unparsed("page", Integer.toString(currentPage)),
                                             Placeholder.unparsed("pages", Integer.toString(pages))))
                    .toList();
        }

    }

    public static class ForwardItem extends PageItem {

        public ForwardItem() {
            super(true);
        }

        @Override
        public ItemProvider getItemProvider(PagedGui<?> gui) {
            ItemBuilder builder;
            if (gui.hasNextPage()) {
                ItemStack item = new ItemStack(Material.ARROW);
                ItemMeta meta = item.getItemMeta();
                assert meta != null;
                meta.itemName(parsedName(gui.getCurrentPage(), gui.getPageAmount()));
                meta.lore(parsedLore(gui.getCurrentPage(), gui.getPageAmount()));
                item.setItemMeta(meta);
                builder = new ItemBuilder(item);
            }
            else builder = new ItemBuilder(blankSlot(Material.GRAY_STAINED_GLASS_PANE));
            return builder;
        }

        private static Component parsedName(int currentPage, int pages) {
            return mm.deserialize(plugin.translatedString("gui.next-page-item.name"),
                                  Placeholder.unparsed("page", Integer.toString(currentPage + 2)),
                                  Placeholder.unparsed("pages", Integer.toString(pages)));
        }

        private static List<Component> parsedLore(int currentPage, int pages) {
            return plugin
                    .translatedStringList("gui.next-page-item.lore")
                    .stream()
                    .map(s -> mm.deserialize(s,
                                             Placeholder.unparsed("page", Integer.toString(currentPage + 2)),
                                             Placeholder.unparsed("pages", Integer.toString(pages))))
                    .toList();
        }

    }

    public static ItemStack blankSlot(Material material) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.itemName(Component.empty());
        item.setItemMeta(meta);
        return item;
    }

}
