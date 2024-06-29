package com.dudko.bazaar.gui;

import com.dudko.bazaar.Bazaar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

import java.util.List;

public class GlobalItems {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final Bazaar plugin = Bazaar.getPlugin();

    public static void init() {
        Structure.addGlobalIngredient('#', blankSlot(Material.GRAY_STAINED_GLASS_PANE));
    }

    public static class BackItem extends PageItem {

        public BackItem() {
            super(false);
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
            return mm.deserialize(plugin.translatedString("gui.previous-page"),
                                  Placeholder.unparsed("page", Integer.toString(currentPage)),
                                  Placeholder.unparsed("pages", Integer.toString(pages)));
        }

        private static List<Component> parsedLore(int currentPage, int pages) {
            return plugin
                    .translatedStringList("gui.previous-page-lore")
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
            return mm.deserialize(plugin.translatedString("gui.next-page"),
                                  Placeholder.unparsed("page", Integer.toString(currentPage + 2)),
                                  Placeholder.unparsed("pages", Integer.toString(pages)));
        }

        private static List<Component> parsedLore(int currentPage, int pages) {
            return plugin
                    .translatedStringList("gui.next-page-lore")
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
