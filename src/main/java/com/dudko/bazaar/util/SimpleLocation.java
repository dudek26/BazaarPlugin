package com.dudko.bazaar.util;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

@SuppressWarnings("unused")
public class SimpleLocation {

    private final double x, y, z;
    private final UUID world;

    public SimpleLocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.world = location.getWorld().getUID();
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public static SimpleLocation deserialize(String json) {
        return new Gson().fromJson(json, SimpleLocation.class);
    }

    public String serialize() {
        return new Gson().toJson(this);
    }
}
