package com.dudko.bazaar.market;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class MarketItem {

    private final UUID id;
    private ItemStack itemStack;
    private double price;
    private List<MarketTax> taxes;
    private OfflinePlayer seller;
    private UUID shopUUID;
    private boolean infinite;
    private final long creationDate;
    private boolean stashed;

    /**
     * Creates a new MarketItem object. Used when creating a new item.
     *
     * @param itemStack The item to sell.
     * @param shopUUID  The UUID of the shop.
     * @param seller    The seller of the item.
     * @param price     The price of the item.
     * @param taxes     The list of taxes to apply.
     */
    public MarketItem(ItemStack itemStack, UUID shopUUID, OfflinePlayer seller, double price, List<MarketTax> taxes) {
        this.id = UUID.randomUUID();
        this.itemStack = itemStack;
        this.price = price;
        this.taxes = taxes;
        this.seller = seller;
        this.shopUUID = shopUUID;
        this.infinite = false;
        this.stashed = false;
        this.creationDate = Instant.now().getEpochSecond();
    }

    /**
     * Creates a new MarketItem object. Used when retrieving items from the database.
     *
     * @param id           The UUID of the item.
     * @param itemStack    The item to sell.
     * @param shopUUID     The UUID of the shop.
     * @param sellerUUID   The UUID of the seller.
     * @param price        The price of the item.
     * @param taxes        The list of taxes to apply.
     * @param infinite     Whether the item has an infinite supply.
     * @param stashed      Whether the item is stashed.
     * @param creationDate The creation date of the item in seconds since the epoch.
     */
    public MarketItem(UUID id, ItemStack itemStack, UUID shopUUID, UUID sellerUUID, double price, List<MarketTax> taxes, boolean infinite, boolean stashed, long creationDate) {
        this.id = id;
        this.itemStack = itemStack;
        this.price = price;
        this.taxes = taxes;
        this.seller = Bukkit.getOfflinePlayer(sellerUUID);
        this.shopUUID = shopUUID;
        this.infinite = infinite;
        this.stashed = stashed;
        this.creationDate = creationDate;
    }

    public UUID getId() {
        return id;
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

    public OfflinePlayer getSeller() {
        return seller;
    }

    public void setSeller(OfflinePlayer sellerUUID) {
        this.seller = sellerUUID;
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

    public String taxesSerialized() {
        return new Gson().toJson(new MarketTax.MarketTaxList(taxes));
    }

    public static List<MarketTax> taxesDeserialized(String taxes) {
        return new Gson().fromJson(taxes, MarketTax.MarketTaxList.class).getMarketTaxes();
    }

    public boolean isTaxIncluded() {
        return taxes.stream().anyMatch(t -> t.getTax() > 0);
    }

    public double getTaxAmount() {
        return taxes.stream().mapToDouble(MarketTax::getTax).sum();
    }
}
