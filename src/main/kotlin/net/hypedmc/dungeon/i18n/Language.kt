package net.hypedmc.dungeon.i18n

import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * Represents supported languages in the plugin.
 * Each language has a code and associated file name.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
enum class Language(
    @NotNull val code: String,
    @NotNull val fileName: String,
    @NotNull val displayName: String
) {
    ENGLISH_US("en_US", "en_US.yml", "English (US)"),
    PORTUGUESE_BR("pt_BR", "pt_BR.yml", "Português (Brasil)"),
    SPANISH("es_ES", "es_ES.yml", "Español");

    companion object {
        /**
         * Gets a language by its code.
         *
         * @param code The language code
         * @return The language or null if not found
         */
        @Nullable
        fun fromCode(@NotNull code: String): Language? {
            return values().find { it.code.equals(code, ignoreCase = true) }
        }

        /**
         * Gets a language by its file name.
         *
         * @param fileName The language file name
         * @return The language or null if not found
         */
        @Nullable
        fun fromFileName(@NotNull fileName: String): Language? {
            return values().find { it.fileName.equals(fileName, ignoreCase = true) }
        }
    }
}
