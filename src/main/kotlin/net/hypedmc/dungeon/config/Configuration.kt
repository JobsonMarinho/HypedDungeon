package net.hypedmc.dungeon.config

import net.hypedmc.dungeon.HypedDungeon
import org.jetbrains.annotations.NotNull

/**
 * Base class for plugin configurations.
 * Provides common functionality for loading and saving config files.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
abstract class Configuration(
    @NotNull protected val plugin: HypedDungeon,
    @NotNull protected val fileName: String
) {
    protected val manager = ConfigurationManager(plugin, fileName)

    /**
     * Loads the configuration from file.
     */
    abstract fun load()

    /**
     * Saves the configuration to file.
     */
    abstract fun save()

    /**
     * Reloads the configuration.
     */
    open fun reload() {
        manager.reload()
        load()
    }
}
