package com.dudko.bazaar.listener;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.item.ItemManager;
import com.dudko.bazaar.shop.GlassDisplayShop;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.UUID;

public class WorldEventListener implements Listener {

    private static final Bazaar plugin = Bazaar.getPlugin();
    private static final MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public static void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (event.getItemInHand().isSimilar(ItemManager.GLASS_DISPLAY_SHOP)) {
            event.setCancelled(true);
            Location location = event.getBlockPlaced().getLocation();

            UUID uuid = GlassDisplayShop.spawn(location);
            player.sendMessage(mm.deserialize(plugin.translatedString("message.shop.created"),
                                              Placeholder.unparsed("id", uuid.toString())));
            if (player.getGameMode() != GameMode.CREATIVE) player.getInventory().remove(event.getItemInHand());
        }
    }

    @EventHandler
    public static void onShopInteraction(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (GlassDisplayShop.isShop(entity)) {

            UUID UUID = GlassDisplayShop.getUUID(entity);
            Player p = event.getPlayer();
            p.openInventory(p.getInventory());
            p.sendMessage(mm.deserialize(
                    "Clicked shop's UUID: <yellow><hover:show_text:'<red>Click to delete</red>'><click:run_command:'/removeshop <uuid>'><uuid></click></hover></yellow>".replace(
                            "<uuid>",
                            UUID.toString())));

        }
    }

}
