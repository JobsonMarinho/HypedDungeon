package net.hypedmc.dungeon

import com.google.inject.Guice
import com.google.inject.Injector
import net.hypedmc.dungeon.config.MainConfig
import net.hypedmc.dungeon.database.DatabaseManager
import net.hypedmc.dungeon.di.InjectorManager
import net.hypedmc.dungeon.i18n.TranslationManager
import net.hypedmc.dungeon.mobs.listeners.CustomMobListener
import net.hypedmc.dungeon.player.PlayerManager
import net.hypedmc.dungeon.service.DungeonManager
import net.hypedmc.dungeon.service.WorldManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.NotNull
import kotlin.system.measureTimeMillis

/**
 * Main plugin class for HypedDungeon.
 * Handles plugin initialization, dependency injection, and lifecycle management.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class HypedDungeon : JavaPlugin(), Listener {
    @NotNull private lateinit var injector: Injector
    @NotNull private lateinit var mainConfig: MainConfig
    @NotNull private lateinit var translationManager: TranslationManager
    @NotNull private lateinit var dungeonManager: DungeonManager
    @NotNull private lateinit var worldManager: WorldManager
    @NotNull private lateinit var customMobListener: CustomMobListener
    @NotNull private lateinit var playerManager: PlayerManager
    @NotNull private lateinit var databaseManager: DatabaseManager

    /**
     * Called when the plugin is enabled.
     * Initializes all necessary components and managers.
     */
    override fun onEnable() {
        val loadTime = measureTimeMillis {
            // Initialize Guice
            InjectorManager.init(this)
            injector = InjectorManager.injector
            injector.injectMembers(this)

            // Initialize database
            databaseManager.init()

            // Load configurations
            mainConfig.load()
            translationManager.load()

            // Register listeners
            server.pluginManager.registerEvents(customMobListener, this)
            server.pluginManager.registerEvents(this, this)

            // Initialize managers
            dungeonManager.init()
            worldManager.init()
            playerManager.init()
        }

        logger.info("Plugin initialized in ${loadTime}ms!")
    }

    /**
     * Called when the plugin is disabled.
     * Performs cleanup and saves all necessary data.
     */
    override fun onDisable() {
        // Save player data
        playerManager.saveAllProfiles()

        // Shutdown managers
        worldManager.shutdown()
        dungeonManager.shutdown()
        databaseManager.shutdown()

        logger.info("Plugin disabled successfully!")
    }

    /**
     * Called when a player quits the server.
     * Saves player data and removes them from the dungeon if they are in one.
     *
     * @param event The PlayerQuitEvent
     */
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        
        // Save player data
        playerManager.getProfile(player).let { profile ->
            playerManager.saveProfile(profile)
            playerManager.removeProfile(player)
        }

        // Remove from dungeon if in one
        dungeonManager.leaveSession(player)
    }
    
}
