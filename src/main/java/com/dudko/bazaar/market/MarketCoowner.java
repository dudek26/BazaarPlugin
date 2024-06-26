package com.dudko.bazaar.market;

import org.bukkit.OfflinePlayer;

import java.util.UUID;

@SuppressWarnings("unused")
public class MarketCoowner {

    private final UUID uniqueID;
    private MarketTax tax;

    public MarketCoowner(OfflinePlayer player, float cut) {
        this.uniqueID = player.getUniqueId();
        this.tax = new MarketTax(player.getUniqueId(), cut);
    }

    public MarketTax getTax() {
        return tax;
    }

    public void setTax(MarketTax tax) {
        this.tax = tax;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }
}
