package net.hypedmc.dungeon.model

import net.hypedmc.dungeon.i18n.TranslationKey
import net.hypedmc.dungeon.i18n.TranslationManager
import org.bukkit.Location
import org.bukkit.entity.Player

/**
 * Represents a dungeon template configuration.
 * Contains all static information about a dungeon type.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
data class DungeonTemplate(
    @NotNull val id: String,
    @NotNull val difficulty: DungeonDifficulty,
    @NotNull val minLevel: Int,
    @NotNull val maxPlayers: Int,
    @NotNull val minPlayers: Int,
    @Nullable val spawnPoint: Location?,
    @NotNull val bossSpawnPoints: Map<String, Location>,
    @NotNull val mobSpawnPoints: List<Location>,
    @NotNull val checkpoints: Map<String, Location>,
    @NotNull val requirements: List<String>,
    @NotNull val rewards: Map<String, Double>
) {
    /**
     * Gets the name of the dungeon.
     *
     * @param translationManager The translation manager
     * @param player The player to get the name for (optional)
     * @return The name of the dungeon
     */
    fun getName(translationManager: TranslationManager, player: Player? = null): String {
        return translationManager.getRaw(
            TranslationKey.DUNGEON_NAME,
            player,
            "{id}" to id
        )
    }

    /**
     * Gets the description of the dungeon.
     *
     * @param translationManager The translation manager
     * @param player The player to get the description for (optional)
     * @return The description of the dungeon
     */
    fun getDescription(translationManager: TranslationManager, player: Player? = null): String {
        return translationManager.getRaw(
            TranslationKey.DUNGEON_DESCRIPTION,
            player,
            "{id}" to id
        )
    }
}
