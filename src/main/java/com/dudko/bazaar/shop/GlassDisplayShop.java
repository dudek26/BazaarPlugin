package com.dudko.bazaar.shop;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.BazaarTags;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.UUID;

public class GlassDisplayShop {

    private static final Bazaar plugin = Bazaar.getPlugin();
    private static final float SIZE = 0.6F;
    private static final float SCALE = (1 - SIZE) / 2;

    public static UUID spawn(Location location) {
        UUID uuid = UUID.randomUUID();

        summonDisplay(location, uuid);
        summonInteractionEntity(location, uuid);
        return uuid;
    }

    private static void summonDisplay(Location location, UUID uuid) {
        BlockDisplay display = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
        display.setBlock(Material.GLASS.createBlockData());
        display.setTransformation(new Transformation(new Vector3f(SCALE, 0, SCALE),
                                                     new AxisAngle4f(),
                                                     new Vector3f(SIZE),
                                                     new AxisAngle4f()));
        PersistentDataContainer data = display.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, BazaarTags.Data.SHOP_UUID), PersistentDataType.STRING, uuid.toString());
    }

    private static void summonInteractionEntity(Location location, UUID uuid) {
        Interaction interaction = (Interaction) location
                .getWorld()
                .spawnEntity(location.add(0.5, 0, 0.5), EntityType.INTERACTION);
        interaction.setInteractionHeight(SIZE);
        interaction.setInteractionWidth(SIZE);
        PersistentDataContainer data = interaction.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, BazaarTags.Data.SHOP_UUID), PersistentDataType.STRING, uuid.toString());
    }

    public static boolean isShop(Entity entity) {
        return entity
                .getPersistentDataContainer()
                .has(new NamespacedKey(plugin, BazaarTags.Data.SHOP_UUID), PersistentDataType.STRING);
    }

    public static UUID getUUID(Entity entity) {
        return UUID.fromString(Objects.requireNonNull(entity
                                                              .getPersistentDataContainer()
                                                              .get(new NamespacedKey(plugin, BazaarTags.Data.SHOP_UUID),
                                                                   PersistentDataType.STRING)));
    }

    public static boolean remove(String uuid) {
        plugin
                .getServer()
                .getWorlds()
                .forEach(world -> world
                        .getEntities()
                        .stream()
                        .filter(GlassDisplayShop::isShop)
                        .filter(entity -> getUUID(entity).toString().equals(uuid))
                        .forEach(Entity::remove));
        return true;
    }

}
