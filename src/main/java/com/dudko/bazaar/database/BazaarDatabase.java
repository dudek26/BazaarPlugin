package com.dudko.bazaar.database;

import com.dudko.bazaar.Bazaar;
import com.dudko.bazaar.market.Market;
import com.dudko.bazaar.market.MarketItem;
import com.dudko.bazaar.market.MarketSettings;
import com.dudko.bazaar.util.SimpleLocation;
import com.google.gson.Gson;

import java.sql.*;
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
                                      CreationDate BIGINT NOT NULL)
                                      """);
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addMarket(Market market) throws SQLException {
        String str = "INSERT INTO Shops (UUID, Owner, Name, Settings, Location, CreationDate) VALUES(?, ?, ?, ?, ?, ?)";
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

    public boolean removeMarket(UUID UUID) throws SQLException {
        if (!marketExists(UUID)) return false;
        String str = "DELETE FROM Shops WHERE uuid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setString(1, UUID.toString());
            preparedStatement.executeUpdate();
            return true;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean marketExists(UUID UUID) throws SQLException {
        String str = "SELECT * FROM Shops WHERE uuid = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(str)) {
            preparedStatement.setString(1, UUID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public Market getMarket(UUID uniqueID) throws SQLException {
        if (!marketExists(uniqueID)) return null;
        String str = "SELECT * FROM Shops WHERE uuid = ?";
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

    public void addMarketItem(MarketItem marketItem) {

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
