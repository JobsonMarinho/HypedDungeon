package net.hypedmc.dungeon.config

import com.google.inject.Inject
import com.google.inject.Singleton
import net.hypedmc.dungeon.HypedDungeon
import org.jetbrains.annotations.NotNull

/**
 * Main configuration for the plugin.
 * Contains general settings and options.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
@Singleton
class MainConfig @Inject constructor(
    @NotNull private val plugin: HypedDungeon
) : Configuration(plugin, "config.yml") {

    /**
     * The default language code for the plugin.
     */
    @NotNull
    val defaultLanguage: String
        get() = manager.getString("settings.default-language", "en_US")

    /**
     * Whether debug mode is enabled.
     */
    val debugMode: Boolean
        get() = manager.getBoolean("settings.debug-mode", false)

    /**
     * The maximum number of dungeons that can be active at once.
     */
    val maxActiveDungeons: Int
        get() = manager.getInt("settings.max-active-dungeons", 10)

    /**
     * The maximum number of players allowed in a dungeon.
     */
    val maxPlayersPerDungeon: Int
        get() = manager.getInt("dungeon.max-players", 4)

    /**
     * The minimum number of players required in a dungeon.
     */
    val minPlayersPerDungeon: Int
        get() = manager.getInt("dungeon.min-players", 1)

    /**
     * The cooldown time for dungeons in seconds.
     */
    val dungeonCooldown: Int
        get() = manager.getInt("dungeon.cooldown", 3600)

    /**
     * Whether to save player data.
     */
    val savePlayerData: Boolean
        get() = manager.getBoolean("data.save-player-data", true)

    /**
     * Loads the main configuration.
     */
    override fun load() {
        defaultLanguage
        debugMode
        maxActiveDungeons
        maxPlayersPerDungeon
        minPlayersPerDungeon
        dungeonCooldown
        savePlayerData
    }

    /**
     * Saves the main configuration.
     */
    override fun save() {
        manager.set("settings.default-language", defaultLanguage)
        manager.set("settings.debug-mode", debugMode)
        manager.set("settings.max-active-dungeons", maxActiveDungeons)
        manager.set("dungeon.max-players", maxPlayersPerDungeon)
        manager.set("dungeon.min-players", minPlayersPerDungeon)
        manager.set("dungeon.cooldown", dungeonCooldown)
        manager.set("data.save-player-data", savePlayerData)
        manager.save()
    }
}
