package com.dudko.bazaar.market;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class MarketItem {

    private ItemStack itemStack;
    private double price;
    private List<MarketTax> taxes;
    private UUID sellerUUID;
    private UUID shopUUID;
    private boolean infinite;
    private final long creationDate;
    private boolean stashed;

    /**
     * Creates a new MarketItem object.
     *
     * @param itemStack The item to sell.
     * @param shopUUID  The UUID of the shop.
     * @param seller    The seller of the item.
     * @param price     The price of the item.
     * @param taxes     The list of taxes to apply.
     */
    public MarketItem(ItemStack itemStack, UUID shopUUID, OfflinePlayer seller, double price, List<MarketTax> taxes) {
        this.itemStack = itemStack;
        this.price = price;
        this.taxes = taxes;
        this.sellerUUID = seller.getUniqueId();
        this.shopUUID = shopUUID;
        this.infinite = false;
        this.stashed = false;
        this.creationDate = Instant.now().getEpochSecond();
    }

    /**
     * Creates a new MarketItem object.
     *
     * @param itemStack    The item to sell.
     * @param shopUUID     The UUID of the shop.
     * @param sellerUUID   The UUID of the seller.
     * @param price        The price of the item.
     * @param taxes        The list of taxes to apply.
     * @param infinite     Whether the item has an infinite supply.
     * @param stashed      Whether the item is stashed.
     * @param creationDate The creation date of the item in seconds since the epoch.
     */
    public MarketItem(ItemStack itemStack, UUID shopUUID, UUID sellerUUID, double price, List<MarketTax> taxes, boolean infinite, boolean stashed, long creationDate) {
        this.itemStack = itemStack;
        this.price = price;
        this.taxes = taxes;
        this.sellerUUID = sellerUUID;
        this.shopUUID = shopUUID;
        this.infinite = infinite;
        this.stashed = stashed;
        this.creationDate = creationDate;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<MarketTax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<MarketTax> tax) {
        this.taxes = tax;
    }

    public UUID getSellerUUID() {
        return sellerUUID;
    }

    public void setSellerUUID(UUID sellerUUID) {
        this.sellerUUID = sellerUUID;
    }

    public UUID getShopUUID() {
        return shopUUID;
    }

    public void setShopUUID(UUID shopUUID) {
        this.shopUUID = shopUUID;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public boolean isStashed() {
        return stashed;
    }

    public void setStashed(boolean stashed) {
        this.stashed = stashed;
    }
}
