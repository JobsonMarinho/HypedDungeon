package net.hypedmc.dungeon.i18n

import com.google.inject.Inject
import com.google.inject.Singleton
import net.hypedmc.dungeon.config.MainConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages translations and localization for the plugin.
 * Handles loading language files and retrieving translated messages.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
@Singleton
class TranslationManager @Inject constructor(
    @NotNull private val plugin: JavaPlugin,
    @NotNull private val mainConfig: MainConfig
) {
    private val translations = ConcurrentHashMap<Language, YamlConfiguration>()
    private val playerLanguages = ConcurrentHashMap<String, Language>()
    private val miniMessage = MiniMessage.miniMessage()

    /**
     * Initializes the translation manager.
     * Loads all available language files.
     */
    init {
        loadAllLanguages()
    }

    /**
     * Loads all language files from the plugin's languages directory.
     */
    private fun loadAllLanguages() {
        Language.values().forEach { language ->
            loadLanguage(language)
        }
    }

    /**
     * Loads a language file from the plugin's languages directory.
     *
     * @param language The language to load
     */
    private fun loadLanguage(language: Language) {
        val file = File(plugin.dataFolder, "lang/${language.fileName}")
        if (!file.exists()) {
            file.parentFile.mkdirs()
            plugin.saveResource("lang/${language.fileName}", false)
        }

        val config = YamlConfiguration.loadConfiguration(file)
        
        // Merge default values from jar
        plugin.getResource("lang/${language.fileName}")?.let { inputStream ->
            val defaultConfig = YamlConfiguration.loadConfiguration(
                InputStreamReader(inputStream, StandardCharsets.UTF_8)
            )
            config.setDefaults(defaultConfig)
        }

        translations[language] = config
    }

    /**
     * Reloads all language files.
     */
    fun reload() {
        translations.clear()
        loadAllLanguages()
    }

    /**
     * Sets a player's preferred language.
     *
     * @param player The player
     * @param language The language to set
     */
    fun setPlayerLanguage(@NotNull player: Player, @NotNull language: Language) {
        playerLanguages[player.uniqueId.toString()] = language
    }

    /**
     * Gets a player's preferred language.
     *
     * @param player The player
     * @return The player's language
     */
    @NotNull
    fun getPlayerLanguage(@NotNull player: Player): Language {
        return playerLanguages[player.uniqueId.toString()]
            ?: Language.fromCode(mainConfig.defaultLanguage)
    }

    /**
     * Gets a translated message for a player.
     *
     * @param key The translation key
     * @param player The player to get the translation for
     * @param placeholders Optional placeholders to replace in the message
     * @return The translated message
     */
    @NotNull
    fun get(
        @NotNull key: TranslationKey,
        @Nullable player: Player? = null,
        @NotNull vararg placeholders: Pair<String, String> = emptyArray()
    ): Component {
        val language = player?.let { getPlayerLanguage(it) }
            ?: Language.fromCode(mainConfig.defaultLanguage)
            
        val config = translations[language]
            ?: translations[Language.ENGLISH_US]
            ?: throw IllegalStateException("Translation not found for language: $language")

        var message = config.getString(key.key)
            ?: translations[Language.ENGLISH_US]?.getString(key.key)
            ?: key.key

        placeholders.forEach { (placeholder, value) ->
            message = message.replace(placeholder, value)
        }

        return miniMessage.deserialize(message)
    }

    /**
     * Gets a raw translated message for a player.
     *
     * @param key The translation key
     * @param player The player to get the translation for
     * @param placeholders Optional placeholders to replace in the message
     * @return The raw translated message
     */
    @NotNull
    fun getRaw(
        @NotNull key: TranslationKey,
        @Nullable player: Player? = null,
        @NotNull vararg placeholders: Pair<String, String> = emptyArray()
    ): String {
        val language = player?.let { getPlayerLanguage(it) }
            ?: Language.fromCode(mainConfig.defaultLanguage)
            
        val config = translations[language]
            ?: translations[Language.ENGLISH_US]
            ?: throw IllegalStateException("Translation not found for language: $language")

        var message = config.getString(key.key)
            ?: translations[Language.ENGLISH_US]?.getString(key.key)
            ?: key.key

        placeholders.forEach { (placeholder, value) ->
            message = message.replace(placeholder, value)
        }

        return message
    }
}
