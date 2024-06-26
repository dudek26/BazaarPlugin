package com.dudko.bazaar.market;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.BazaarTags;
import com.dudko.bazaar.database.BazaarDatabase;
import com.dudko.bazaar.util.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings({"unused", "CallToPrintStackTrace"})
public class Market {

    private static final Bazaar plugin = Bazaar.getPlugin();
    private final BazaarDatabase database = plugin.getDatabase();

    private final UUID uniqueID;
    private OfflinePlayer owner;
    private String name;
    private MarketSettings settings;
    private SimpleLocation simpleLocation;
    private final long creationDate;

    public Market(UUID uuid, OfflinePlayer player, String name, MarketSettings settings, SimpleLocation simpleLocation, long creationDate) {
        this.uniqueID = uuid;
        this.owner = player;
        this.name = name;
        this.settings = settings;
        this.simpleLocation = simpleLocation;
        this.creationDate = creationDate;
    }

    public Market(OfflinePlayer player, String name, MarketSettings settings, SimpleLocation simpleLocation) {
        this.uniqueID = java.util.UUID.randomUUID();
        this.owner = player;
        this.name = name;
        this.settings = settings;
        this.simpleLocation = simpleLocation;
        this.creationDate = Instant.now().getEpochSecond();
    }

    public SimpleLocation getSimpleLocation() {
        return simpleLocation;
    }

    public UUID getUUID() {
        return uniqueID;
    }

    public OfflinePlayer getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public MarketSettings getSettings() {
        return settings;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSettings(MarketSettings settings) {
        this.settings = settings;
    }

    public void setSimpleLocation(SimpleLocation simpleLocation) {
        this.simpleLocation = simpleLocation;
    }

    public void setOwner(OfflinePlayer owner) {
        this.owner = owner;
    }

    /**
     * Creates the shop.
     */
    public void create() {
        try {
            database.addMarket(this);
            summonBlockDisplay(this.simpleLocation.toLocation(),
                               this.uniqueID,
                               settings.getMaterial().createBlockData());
            summonInteractionEntity(this.simpleLocation.toLocation(), this.uniqueID);
            summonItemDisplay(this.simpleLocation.toLocation(), this.uniqueID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes the shop.
     *
     * @return True if the shop was successfully removed, false otherwise.
     */
    public boolean remove() {
        removeEntities(uniqueID);
        boolean success;
        try {
            success = database.removeMarket(uniqueID);
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private static final float SIZE = 0.6F;
    private static final float SCALE = (1 - SIZE) / 2;

    private static void summonBlockDisplay(Location location, UUID uuid, BlockData blockData) {
        BlockDisplay display = (BlockDisplay) location.getWorld().spawnEntity(location, EntityType.BLOCK_DISPLAY);
        display.setBlock(blockData);
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

    private static void summonItemDisplay(Location location, UUID uuid) {
        ItemDisplay display = (ItemDisplay) location
                .getWorld()
                .spawnEntity(location.add(0.5, 0, 0.5), EntityType.ITEM_DISPLAY);
        display.setItemStack(new ItemStack(Material.IRON_SWORD));
        display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.GROUND);
        display.setTransformation(new Transformation(new Vector3f(0, SCALE - 0.125F, 0),
                                                     new AxisAngle4f(),
                                                     new Vector3f(SIZE + 0.25F),
                                                     new AxisAngle4f()));
        display.setBillboard(Display.Billboard.VERTICAL);
        PersistentDataContainer data = display.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, BazaarTags.Data.SHOP_UUID), PersistentDataType.STRING, uuid.toString());
    }

    /**
     * Checks if an entity is a shop.
     *
     * @param entity The entity to check.
     * @return True if the entity is a shop, false otherwise.
     */
    public static boolean isShop(Entity entity) {
        return entity
                .getPersistentDataContainer()
                .has(new NamespacedKey(plugin, BazaarTags.Data.SHOP_UUID), PersistentDataType.STRING);
    }

    /**
     * Gets the UUID of a shop from its persistent data container.
     *
     * @param entity The entity to get the UUID from.
     * @return The UUID of the shop or null if provided entity is not a shop.
     */
    @Nullable
    public static UUID getUUID(Entity entity) {
        if (!isShop(entity)) return null;
        return java.util.UUID.fromString(Objects.requireNonNull(entity
                                                                        .getPersistentDataContainer()
                                                                        .get(new NamespacedKey(plugin,
                                                                                               BazaarTags.Data.SHOP_UUID),
                                                                             PersistentDataType.STRING)));
    }

    private static void removeEntities(UUID uuid) {
        plugin
                .getServer()
                .getWorlds()
                .forEach(world -> world
                        .getEntities()
                        .stream()
                        .filter(Market::isShop)
                        .filter(entity -> Objects.requireNonNull(getUUID(entity)).toString().equals(uuid.toString()))
                        .forEach(Entity::remove));
    }

}
