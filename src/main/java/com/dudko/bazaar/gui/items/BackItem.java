package com.dudko.bazaar.gui.items;

import com.dudko.bazaar.Bazaar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

import java.util.List;

public class BackItem extends PageItem {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private final Bazaar plugin = Bazaar.getPlugin();

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
        else return new BlankItem(Material.GRAY_STAINED_GLASS_PANE).getItemProvider();
        return builder;
    }

    private Component parsedName(int currentPage, int pages) {
        return mm.deserialize(plugin.translatedString("gui.previous-page-item.name"),
                              Placeholder.unparsed("page", Integer.toString(currentPage)),
                              Placeholder.unparsed("pages", Integer.toString(pages)));
    }

    private List<Component> parsedLore(int currentPage, int pages) {
        return plugin
                .translatedStringList("gui.previous-page-item.lore")
                .stream()
                .map(s -> mm.deserialize(s,
                                         Placeholder.unparsed("page", Integer.toString(currentPage)),
                                         Placeholder.unparsed("pages", Integer.toString(pages))))
                .toList();
    }

}
