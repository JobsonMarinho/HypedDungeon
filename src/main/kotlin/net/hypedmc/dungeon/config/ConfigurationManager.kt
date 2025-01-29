package net.hypedmc.dungeon.config

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * Manages a single configuration file for the plugin.
 * Handles loading, saving, and reloading of the configuration.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class ConfigurationManager(
    private val plugin: org.bukkit.plugin.java.JavaPlugin,
    private val fileName: String
) {
    private var config: YamlConfiguration = YamlConfiguration()
    private val configFile: File = File(plugin.dataFolder, fileName)

    /**
     * Initializes the configuration.
     */
    init {
        load()
    }

    /**
     * Loads the configuration file.
     */
    fun load() {
        plugin.dataFolder.mkdirs()
        
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false)
        }

        config = YamlConfiguration.loadConfiguration(configFile)
        
        // Merge default values from jar
        plugin.getResource(fileName)?.let { inputStream ->
            val defaultConfig = YamlConfiguration.loadConfiguration(
                InputStreamReader(inputStream, StandardCharsets.UTF_8)
            )
            config.setDefaults(defaultConfig)
        }
    }

    /**
     * Saves the configuration file.
     */
    fun save() {
        config.save(configFile)
    }

    /**
     * Gets a string value from the configuration.
     *
     * @param path The path to the value
     * @return The string value
     */
    fun getString(path: String): String = config.getString(path) ?: ""

    /**
     * Gets a list of string values from the configuration.
     *
     * @param path The path to the values
     * @return The list of string values
     */
    fun getStringList(path: String): List<String> = config.getStringList(path)

    /**
     * Gets an integer value from the configuration.
     *
     * @param path The path to the value
     * @return The integer value
     */
    fun getInt(path: String): Int = config.getInt(path)

    /**
     * Gets a double value from the configuration.
     *
     * @param path The path to the value
     * @return The double value
     */
    fun getDouble(path: String): Double = config.getDouble(path)

    /**
     * Gets a boolean value from the configuration.
     *
     * @param path The path to the value
     * @return The boolean value
     */
    fun getBoolean(path: String): Boolean = config.getBoolean(path)

    /**
     * Gets a configuration section.
     *
     * @param path The path to the section
     * @return The configuration section
     */
    fun getSection(path: String) = config.getConfigurationSection(path)
    
    /**
     * Sets a value in the configuration.
     *
     * @param path The path to the value
     * @param value The value to set
     */
    fun set(path: String, value: Any?) {
        config.set(path, value)
    }

    /**
     * Reloads the configuration file.
     */
    fun reload() {
        load()
    }
}
