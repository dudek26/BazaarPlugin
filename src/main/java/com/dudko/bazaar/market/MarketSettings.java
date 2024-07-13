package com.dudko.bazaar.market;

import com.google.gson.Gson;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class MarketSettings {

    private Material block;
    private boolean admin;
    private double tax;
    private List<MarketCoowner> coowners;

    /**
     * Constructor for the settings of a shop.
     *
     * @param admin Whether this is an admin shop or not.
     */
    public MarketSettings(boolean admin) {
        this.admin = admin;
        this.block = Material.GLASS;
        this.tax = 0;
        this.coowners = new ArrayList<>();
    }

    /**
     * Sets whether the shop is an admin shop or not.
     *
     * @param bool True if the shop is an admin shop, false otherwise.
     */
    public void setAdmin(boolean bool) {
        this.admin = bool;
    }

    /**
     * @return Whether the shop is an admin shop or not.
     */
    public boolean isAdmin() {
        return this.admin;
    }

    /**
     * @return JSON representation of the shop settings.
     */
    public String serialize() {
        return new Gson().toJson(this);
    }

    public Material getMaterial() {
        return block;
    }

    /**
     * Sets the material that will be used as a case of the shop.
     *
     * @param material Material of the block to be set.
     */
    public void setMaterial(Material material) {
        if (material.isBlock()) this.block = material;
    }

    /**
     * @return Cut percentage of the shop's transactions. The cut would go to the owner's account.
     */
    public double getTax() {
        return tax;
    }

    /**
     * Sets the cut percentage of the shop's transactions. The cut would go to the owner's account.
     *
     * @param tax Cut percentage, must be at least 0.0 and at most 1.0.
     */
    public void setTax(float tax) {
        this.tax = tax > 1 ? 1 : tax < 0 ? 0 : tax;
    }

    /**
     * Gets the co-owners of the shop.
     *
     * @return List of co-owners.
     */
    public List<MarketCoowner> getCoOwners() {
        return coowners;
    }

    /**
     * Sets the co-owners of the shop.
     * This will overwrite any existing co-owners.
     *
     * @param coowners List of co-owners.
     */
    public void setCoOwners(List<MarketCoowner> coowners) {
        this.coowners = coowners;
    }

    /**
     * Adds a co-owner to the shop.
     *
     * @param p   Player to add as a co-owner.
     * @param cut Cut percentage of the shop's transactions for the co-owner.
     */
    public void addCoOwner(OfflinePlayer p, float cut) {
        this.coowners.add(new MarketCoowner(p, cut));
    }

    /**
     * Removes a co-owner from the shop.
     *
     * @param p Player to remove as a co-owner.
     */
    public void removeCoOwner(OfflinePlayer p) {
        this.coowners.stream().filter(c -> c.getUniqueID().equals(p.getUniqueId())).forEach(this.coowners::remove);
    }

    /**
     * Checks if a player is a co-owner of the shop.
     *
     * @param p Player to check.
     * @return True if the player is a co-owner, false otherwise.
     */
    public boolean isCoOwner(OfflinePlayer p) {
        return this.coowners.stream().anyMatch(c -> c.getUniqueID().equals(p.getUniqueId()));
    }

    /**
     * Gets the co-owner object of a player.
     *
     * @param p Player to get the co-owner of.
     * @return The co-owner object of the player, or null if not found.
     */
    @Nullable
    public MarketCoowner getCoOwner(OfflinePlayer p) {
        return this.coowners.stream().filter(c -> c.getUniqueID().equals(p.getUniqueId())).findFirst().orElse(null);
    }

    /**
     * Deserializes a JSON string into a ShopSettings object.
     *
     * @param json JSON string to deserialize.
     * @return ShopSettings object deserialized from the JSON string.
     */
    public static MarketSettings deserialize(String json) {
        return new Gson().fromJson(json, MarketSettings.class);
    }


}
