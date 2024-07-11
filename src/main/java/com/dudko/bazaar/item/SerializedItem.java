package com.dudko.bazaar.item;

import com.dudko.bazaar.Bazaar;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SerializedItem {

    public String itemTypeKey;
    public String components;
    public int quantity;

    public SerializedItem(ItemStack item) {
        if (item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        this.itemTypeKey = item.getType().getKey().asString();
        this.components = meta.getAsComponentString();
        this.quantity = item.getAmount();
    }

    public String serialize() {
        return new Gson().toJson(this);
    }

    public static SerializedItem deserialize(String json) {
        return new Gson().fromJson(json, SerializedItem.class);
    }

    public ItemStack toItemStack() {
        return Bukkit.getItemFactory().createItemStack(itemTypeKey + components).asQuantity(quantity);
    }

}
