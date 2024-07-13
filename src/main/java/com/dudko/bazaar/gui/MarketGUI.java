package com.dudko.bazaar.gui;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.market.Market;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

public abstract class MarketGUI {

    private final Market market;
    private final Player viewer;

    public MarketGUI(Market market, Player viewer) {
        this.market = market;
        this.viewer = viewer;
    }

    /**
     * Safely close a player's inventory. Used in Inventory Events.
     *
     * @param player The player to close the inventory for.
     */
    public static void safeCloseInventory(@NotNull Player player) {
        Bukkit
                .getScheduler()
                .runTask(Bazaar.getPlugin(), () -> player.closeInventory(InventoryCloseEvent.Reason.PLUGIN));
    }

    public void display() {

    }

    public Market getMarket() {
        return market;
    }
    public Player getViewer() {
        return viewer;
    }

}
