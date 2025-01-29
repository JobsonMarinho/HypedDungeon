package net.hypedmc.dungeon.di

import com.google.inject.Guice
import com.google.inject.Injector
import net.hypedmc.dungeon.HypedDungeon
import org.jetbrains.annotations.NotNull

/**
 * Manages dependency injection for the plugin.
 * Handles creation and access to the Guice injector.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
object InjectorManager {
    private lateinit var injector: Injector

    /**
     * Creates the injector with the main module.
     *
     * @param plugin The plugin instance
     */
    fun createInjector(@NotNull plugin: HypedDungeon) {
        injector = Guice.createInjector(MainModule(plugin))
    }

    /**
     * Gets an instance of a class from the injector.
     *
     * @param clazz The class to get an instance of
     * @return The instance of the class
     */
    @NotNull
    fun <T> getInstance(@NotNull clazz: Class<T>): T {
        return injector.getInstance(clazz)
    }

    /**
     * Injects dependencies into an instance.
     *
     * @param instance The instance to inject dependencies into
     */
    fun injectMembers(@NotNull instance: Any) {
        injector.injectMembers(instance)
    }
}
