package net.hypedmc.dungeon.di

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import net.hypedmc.dungeon.HypedDungeon
import net.hypedmc.dungeon.config.ConfigurationManager
import net.hypedmc.dungeon.i18n.TranslationManager
import net.hypedmc.dungeon.mobs.MobManager
import net.hypedmc.dungeon.player.PlayerManager
import net.hypedmc.dungeon.service.DungeonManager
import net.hypedmc.dungeon.service.WorldManager
import org.jetbrains.annotations.NotNull

/**
 * Main dependency injection module for the plugin.
 * Configures all service bindings and their scopes.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class MainModule(
    @NotNull private val plugin: HypedDungeon
) : AbstractModule() {

    /**
     * Configures all service bindings.
     */
    override fun configure() {
        // Bind plugin instance
        bind(HypedDungeon::class.java)
            .toInstance(plugin)

        // Bind managers as singletons
        bind(ConfigurationManager::class.java)
            .`in`(Scopes.SINGLETON)
            
        bind(TranslationManager::class.java)
            .`in`(Scopes.SINGLETON)
            
        bind(PlayerManager::class.java)
            .`in`(Scopes.SINGLETON)
            
        bind(DungeonManager::class.java)
            .`in`(Scopes.SINGLETON)
            
        bind(WorldManager::class.java)
            .`in`(Scopes.SINGLETON)
            
        bind(MobManager::class.java)
            .`in`(Scopes.SINGLETON)
    }

    /**
     * Provides the main configuration.
     *
     * @param plugin The plugin instance
     * @return The main configuration
     */
    @Provides
    @Singleton
    fun provideMainConfig(@NotNull plugin: HypedDungeon): MainConfig {
        return MainConfig(plugin)
    }
}
