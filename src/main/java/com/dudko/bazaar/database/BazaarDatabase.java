package com.dudko.bazaar.database;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.item.SerializedItem;
import com.dudko.bazaar.market.Market;
import com.dudko.bazaar.market.MarketItem;
import com.dudko.bazaar.market.MarketSettings;
import com.dudko.bazaar.util.SimpleLocation;
import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BazaarDatabase {

    private final Connection connection;
    private static final Bazaar plugin = Bazaar.getPlugin();

    public BazaarDatabase(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                                      CREATE TABLE IF NOT EXISTS Markets (
                                      UUID TEXT PRIMARY KEY,
                                      Owner TEXT NOT NULL,
                                      Name TEXT NOT NULL,
                                      Settings TEXT NOT NULL,
                                      Location TEXT NOT NULL,
                                      CreationDate BIGINT NOT NULL,
                                      Profit REAL NOT NULL DEFAULT 0,
                                      Taxes REAL NOT NULL DEFAULT 0)
                                      """);
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                                      CREATE TABLE IF NOT EXISTS MarketItems (
                                      UUID TEXT PRIMARY KEY,
                                      Shop TEXT NOT NULL,
                                      Seller TEXT NOT NULL,
                                      ItemStack TEXT NOT NULL,
                                      Infinite BOOLEAN NOT NULL DEFAULT 0,
                                      Price REAL NOT NULL DEFAULT 1,
                                      Taxes TEXT NOT NULL,
                                      CreationDate BIGINT NOT NULL,
                                      Stashed BOOLEAN NOT NULL DEFAULT 0)
                                      """);
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addMarket(Market market) throws SQLException {
        String str = "INSERT INTO Markets (UUID, Owner, Name, Settings, Location, CreationDate) VALUES(?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setString(1, market.getUUID().toString());
            preparedStatement.setString(2, market.getOwner().getUniqueId().toString());
            preparedStatement.setString(3, market.getName());
            preparedStatement.setString(4, market.getSettings().serialize());
            preparedStatement.setString(5, market.getSimpleLocation().serialize());
            preparedStatement.setLong(6, market.getCreationDate());
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Remove a market from the database
     *
     * @param uniqueID the UUID of the market to remove
     * @return {@code true} if the market was removed, {@code false} if it didn't exist
     */
    public boolean removeMarket(UUID uniqueID) throws SQLException {
        if (!marketExists(uniqueID)) return false;
        String str = "DELETE FROM Markets WHERE UUID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setString(1, uniqueID.toString());
            preparedStatement.executeUpdate();
            return true;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean marketExists(UUID uniqueID) throws SQLException {
        String str = "SELECT * FROM Markets WHERE UUID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setString(1, uniqueID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public Market getMarket(UUID uniqueID) throws SQLException {
        if (!marketExists(uniqueID)) return null;
        String str = "SELECT * FROM Markets WHERE UUID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setString(1, uniqueID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return new Market(uniqueID,
                              plugin.getServer().getOfflinePlayer(UUID.fromString(resultSet.getString("Owner"))),
                              resultSet.getString("Name"),
                              MarketSettings.deserialize(resultSet.getString("Settings")),
                              new Gson().fromJson(resultSet.getString("Location"), SimpleLocation.class),
                              resultSet.getLong("CreationDate"));
        }
    }

    public void addMarketItem(MarketItem marketItem) throws SQLException {
        if (!marketExists(marketItem.getShopUUID())) return;
        String str = "INSERT INTO MarketItems (UUID, Shop, Seller, ItemStack, Price, Taxes, CreationDate, Infinite) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setString(1, marketItem.getId().toString());
            preparedStatement.setString(2, marketItem.getShopUUID().toString());
            preparedStatement.setString(3, marketItem.getSeller().getUniqueId().toString());
            preparedStatement.setString(4, new SerializedItem(marketItem.getItemStack()).serialize());
            preparedStatement.setDouble(5, marketItem.getPrice());
            preparedStatement.setString(6, marketItem.taxesSerialized());
            preparedStatement.setLong(7, marketItem.getCreationDate());
            preparedStatement.setBoolean(8, marketItem.isInfinite());
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Remove a market item from the database
     *
     * @param uniqueID the UUID of the market item to remove
     */
    public void removeMarketItem(UUID uniqueID) throws SQLException {
        if (!marketItemExists(uniqueID)) return;
        String str = "DELETE FROM MarketItems WHERE UUID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setString(1, uniqueID.toString());
            preparedStatement.executeUpdate();
        }
    }

    public boolean marketItemExists(UUID uniqueID) throws SQLException {
        String str = "SELECT * FROM MarketItems WHERE UUID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setString(1, uniqueID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public List<MarketItem> getMarketItems(UUID shopUUID) throws SQLException {
        String str = "SELECT * FROM MarketItems WHERE Shop = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            List<MarketItem> items = new ArrayList<>();

            preparedStatement.setString(1, shopUUID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                items.add(new MarketItem(UUID.fromString(resultSet.getString("UUID")),
                                         SerializedItem.deserialize(resultSet.getString("ItemStack")).toItemStack(),
                                         UUID.fromString(resultSet.getString("Shop")),
                                         UUID.fromString(resultSet.getString("Seller")),
                                         resultSet.getDouble("Price"),
                                         MarketItem.taxesDeserialized(resultSet.getString("Taxes")),
                                         resultSet.getBoolean("Infinite"),
                                         resultSet.getBoolean("Stashed"),
                                         resultSet.getLong("CreationDate")));
            }
            return items;
        }
    }

    /*
    public enum Table {
        SHOPS;

        public final String name;

        Table() {
            this.name = this.toString().toLowerCase().replace("_", "");
        }
    }
     */

}
