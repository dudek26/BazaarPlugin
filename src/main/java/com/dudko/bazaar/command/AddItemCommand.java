package com.dudko.bazaar.command;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.market.Market;
import com.dudko.bazaar.market.MarketItem;
import com.dudko.bazaar.market.MarketTax;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("CallToPrintStackTrace")
public class AddItemCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    private static final Bazaar plugin = Bazaar.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args.length < 1 || !(sender instanceof Player p)) {
            sender.sendMessage(mm.deserialize(plugin.translatedString("message.command.usage"),
                                              Placeholder.unparsed("usage", command.getUsage())));

            return true;
        }

        if (args[0].equalsIgnoreCase("test")) {
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item.getType().isAir()) return false;

            ItemMeta meta = item.getItemMeta();
            String components = meta.getAsComponentString();
            plugin.getLogger().info(components);
            p.getInventory().addItem(Bukkit.getItemFactory().createItemStack("minecraft:stick" + components).asQuantity(5));
            return true;
        }

        try {
            UUID uniqueID = UUID.fromString(args[0]);
            Market market = plugin.getDatabase().getMarket(uniqueID);
            MarketItem marketItem = getMarketItem(p, market, uniqueID);
            plugin.getDatabase().addMarketItem(marketItem);

        } catch (IllegalArgumentException e) {
            sender.sendMessage(parsedComponent("message.shop.removed-error", args[0]));
        } catch (SQLException e) {
            sender.sendMessage(parsedComponent("message.shop.db-error", args[0]));
            e.printStackTrace();
        }

        return true;
    }

    private static @NotNull MarketItem getMarketItem(Player sender, Market market, UUID uniqueID) {
        if (market == null) throw new IllegalArgumentException();
        ItemStack item = sender.getInventory().getItemInMainHand();
        if (item.getType().isAir()) throw new IllegalArgumentException();

        return new MarketItem(item, uniqueID, sender, 100, List.of(new MarketTax(sender.getUniqueId(), 0.2)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return List.of();
    }

    private Component parsedComponent(String key, String id) {
        return mm.deserialize(plugin.translatedString(key), Placeholder.unparsed("id", id));
    }
}
