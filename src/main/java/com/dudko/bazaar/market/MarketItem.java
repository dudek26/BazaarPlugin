package com.dudko.bazaar.market;

import com.dudko.bazaar.Bazaar;
import com.google.gson.Gson;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings({"unused", "CallToPrintStackTrace"})
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

    public double getTaxedPrice() {
        return price - price * getTaxAmount();
    }

    /**
     * Tries to buy the item from the market.
     *
     * @param player The player buying the item.
     * @return Result of the purchase.
     */
    public BuyResult buy(Player player) {
        try {
            if (!Bazaar.getPlugin().getDatabase().marketItemExists(id)) return BuyResult.ITEM_DOES_NOT_EXIST;
            if (!Bazaar.getPlugin().getDatabase().marketExists(shopUUID)) return BuyResult.MARKET_DOES_NOT_EXIST;
            if (stashed) return BuyResult.ITEM_DOES_NOT_EXIST;

            Economy econ = Bazaar.getEconomy();
            Market market = Bazaar.getPlugin().getDatabase().getMarket(shopUUID);
            Location marketLocation = market.getSimpleLocation().toLocation();
            String worldName = marketLocation.getWorld().getName();

            if (!econ.has(player, worldName, price)) return BuyResult.NOT_ENOUGH_MONEY;

//            if (player.getInventory().firstEmpty() == -1) return BuyResult.NO_INVENTORY_SPACE;
            if (player.getInventory().firstEmpty() == -1) {
                if (itemStack.getMaxStackSize() == 1) return BuyResult.NO_INVENTORY_SPACE;
                if (Arrays
                        .stream(player.getInventory().getStorageContents())
                        .filter(Objects::nonNull)
                        .noneMatch(i -> i.isSimilar(itemStack)
                                        && i.getMaxStackSize() >= i.getAmount() + itemStack.getAmount()))
                    return BuyResult.NO_INVENTORY_SPACE;
            }

            if (!infinite) Bazaar.getPlugin().getDatabase().removeMarketItem(id);
            econ.withdrawPlayer(player, worldName, price);
            econ.depositPlayer(seller, worldName, getTaxedPrice());

            taxes.forEach(tax -> econ.depositPlayer(Bukkit.getOfflinePlayer(tax.getReceiver()),
                                                    worldName,
                                                    tax.calculateTax(price)));

            player.getInventory().addItem(itemStack);

            return BuyResult.SUCCESS;
        } catch (SQLException e) {
            e.printStackTrace();
            return BuyResult.SQL_EXCEPTION;
        }
    }

    public enum BuyResult {
        SUCCESS,
        NOT_ENOUGH_MONEY,
        NO_INVENTORY_SPACE,
        ITEM_DOES_NOT_EXIST,
        MARKET_DOES_NOT_EXIST,
        SQL_EXCEPTION
    }
}
