package com.dudko.bazaar.listener;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.gui.MarketGUI;
import com.dudko.bazaar.item.ItemManager;
import com.dudko.bazaar.market.Market;
import com.dudko.bazaar.market.MarketSettings;
import com.dudko.bazaar.util.SimpleLocation;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.sql.SQLException;
import java.util.UUID;

public class WorldEventListener implements Listener {

    private static final Bazaar plugin = Bazaar.getPlugin();
    private static final MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public static void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (event.getItemInHand().isSimilar(ItemManager.MARKET)) {
            event.setCancelled(true);
            Location location = event.getBlockPlaced().getLocation();

            Market market = new Market(player,
                                       player.getName() + "'s Shop",
                                       new MarketSettings(false),
                                       new SimpleLocation(location));
            market.create();
            player.sendMessage(mm.deserialize(plugin.translatedString("message.shop.created"),
                                              Placeholder.unparsed("id", market.getUUID().toString())));
            if (player.getGameMode() != GameMode.CREATIVE) player.getInventory().remove(event.getItemInHand());
        }
        else if (event.getItemInHand().isSimilar(ItemManager.ADMIN_MARKET)) {
            event.setCancelled(true);
            Location location = event.getBlockPlaced().getLocation();

            Market market = new Market(player, "Admin Shop", new MarketSettings(true), new SimpleLocation(location));
            market.getSettings().setMaterial(Material.RED_STAINED_GLASS);
            market.create();
            player.sendMessage(mm.deserialize(plugin.translatedString("message.shop.created"),
                                              Placeholder.unparsed("id", market.getUUID().toString())));
            if (player.getGameMode() != GameMode.CREATIVE) player.getInventory().remove(event.getItemInHand());
        }

    }

    @EventHandler
    public static void onShopInteraction(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (Market.isShop(entity)) {

            UUID UUID = Market.getUUID(entity);
            assert UUID != null;
            try {
                if (!plugin.getDatabase().marketExists(UUID)) return;
                Market market = plugin.getDatabase().getMarket(UUID);
                Player p = event.getPlayer();
                new MarketGUI(market).display(p);
                p.sendMessage(mm.deserialize(
                        "Clicked shop's UUID: <yellow><hover:show_text:'<white>Click to copy the UUID</white>'><click:suggest_command:'<uuid>'><uuid></click></hover></yellow>".replace(
                                "<uuid>",
                                UUID.toString())));
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
    }

}
