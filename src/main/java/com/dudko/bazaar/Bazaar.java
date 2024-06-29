package com.dudko.bazaar;

import com.dudko.bazaar.command.AddItemCommand;
import com.dudko.bazaar.command.BazaarCommand;
import com.dudko.bazaar.command.RemoveMarketCommand;
import com.dudko.bazaar.database.BazaarDatabase;
import com.dudko.bazaar.gui.GlobalItems;
import com.dudko.bazaar.item.ItemManager;
import com.dudko.bazaar.listener.WorldEventListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@SuppressWarnings({"ResultOfMethodCallIgnored", "CallToPrintStackTrace", "UnstableApiUsage"})
public final class Bazaar extends JavaPlugin {

    private static Bazaar plugin;

    private FileConfiguration localisation;
    private FileConfiguration defaultLocalisation;

    private BazaarDatabase database;

    /**
     * Returns the instance of the plugin.
     *
     * @return Instance of the plugin.
     */
    public static Bazaar getPlugin() {
        return plugin;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        loadDefaultLocalisation();
        loadLocalisation(getConfig().getString("localisation"));

        try {

            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }

            database = new BazaarDatabase(getDataFolder().getAbsolutePath() + "/data.db");
            Bukkit.getServer().getLogger().info("[Bazaar] " + translatedString("debug.database.connection-successful"));
        } catch (SQLException ex) {
            ex.printStackTrace();
            Bukkit
                    .getServer()
                    .getLogger()
                    .severe("[Bazaar] " + translatedString("debug.database.connection-failed") + " " + ex.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        GlobalItems.init();

        getCommand("bazaar").setExecutor(new BazaarCommand());
        getCommand("removemarket").setExecutor(new RemoveMarketCommand());
        getCommand("additem").setExecutor(new AddItemCommand());

        getServer().getPluginManager().registerEvents(new WorldEventListener(), this);

        new ItemManager().registerItems();
    }

    @Override
    public void onDisable() {
        try {
            database.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the localisation file.
     *
     * @param useDefault Whether to use the default localisation file or not.
     * @return FileConfiguration of the localisation.
     */
    @SuppressWarnings("SameParameterValue")
    private FileConfiguration getLocalisation(boolean useDefault) {
        return useDefault ? this.defaultLocalisation : this.localisation;
    }

    /**
     * Gets the localisation file.
     *
     * @return FileConfiguration of the localisation.
     */
    private FileConfiguration getLocalisation() {
        return this.localisation;
    }

    /**
     * Creates the localisation files if they don't exist
     *
     * @return Default localisation File
     */
    private File createLocalisationFiles() {
        File english = new File(getDataFolder() + "/localisation", "en.yml");
        if (!english.exists()) {
            english.getParentFile().mkdirs();
            saveResource("localisation/en.yml", false);
        }
        return english;
    }

    /**
     * Loads the localisation file for the specified language. If the file doesn't exist, loads the default localisation file.
     *
     * @param lang Localisation to be loaded.
     */
    private void loadLocalisation(String lang) {
        File langFile = new File(getDataFolder() + "/localisation", lang + ".yml");
        if (!langFile.exists()) {
            this.localisation = defaultLocalisation;
            return;
        }

        FileConfiguration loadedLocalisation = new YamlConfiguration();
        try {
            loadedLocalisation.load(langFile);
            this.localisation = loadedLocalisation;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the default localisation file.
     */
    private void loadDefaultLocalisation() {
        FileConfiguration loadedLocalisation = new YamlConfiguration();
        try {
            loadedLocalisation.load(createLocalisationFiles());
            this.defaultLocalisation = loadedLocalisation;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Translates a string using the localisation file.
     *
     * @param key Key of the string to be translated.
     * @return Translated string. If the key is not found, the key is translated in the default language. If the key is still not found, the key is returned.
     */
    public String translatedString(String key) {
        String string = getLocalisation().getString(key);
        if (string == null) getLocalisation(true).getString(key);
        if (string == null) string = key;
        return string;
    }

    /**
     * Translates a list of strings using the localisation file.
     *
     * @param key Key of the list of strings to be translated.
     * @return List of translated strings. If the key is not found, the key is translated in the default language. If the key is still not found, returns a list containing the untranslated key.
     */
    public List<String> translatedStringList(String key) {
        List<String> list = getLocalisation().getStringList(key);
        if (list.isEmpty()) getLocalisation(true).getStringList(key);
        if (list.isEmpty()) list.add(key);
        return list;
    }


    public BazaarDatabase getDatabase() {
        return database;
    }
}
