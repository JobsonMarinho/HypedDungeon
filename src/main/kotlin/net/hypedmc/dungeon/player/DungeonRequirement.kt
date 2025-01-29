package net.hypedmc.dungeon.player

import net.hypedmc.dungeon.i18n.TranslationKey
import net.hypedmc.dungeon.i18n.TranslationManager
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull

/**
 * Represents a requirement for entering a dungeon.
 * Requirements can be level-based, completion-based, or achievement-based.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
sealed class DungeonRequirement {
    /**
     * Checks if a player profile meets this requirement.
     *
     * @param profile The player profile to check
     * @return True if the requirement is met, false otherwise
     */
    abstract fun check(@NotNull profile: PlayerProfile): Boolean

    /**
     * Gets a description of the requirement.
     *
     * @param translationManager The translation manager to use
     * @param player The player to get the description for
     * @return A human-readable description of the requirement
     */
    @NotNull
    abstract fun getDescription(translationManager: TranslationManager, player: Player? = null): String

    companion object {
        /**
         * Creates a dungeon requirement from a string specification.
         *
         * @param spec The requirement specification string
         * @return The created requirement
         * @throws IllegalArgumentException if the specification is invalid
         */
        @NotNull
        fun fromString(@NotNull spec: String): DungeonRequirement {
            val parts = spec.split(":")
            return when (parts[0].lowercase()) {
                "level" -> LevelRequirement(parts[1].toInt())
                "completion" -> CompletionsRequirement(parts[1], parts[2].toInt())
                "achievement" -> AchievementRequirement(parts[1])
                else -> throw IllegalArgumentException("Invalid requirement type: ${parts[0]}")
            }
        }
    }
}

/**
 * Represents a level-based dungeon requirement.
 *
 * @property level The minimum level required
 */
data class LevelRequirement(
    @NotNull val level: Int
) : DungeonRequirement() {
    override fun check(profile: PlayerProfile): Boolean {
        return profile.level >= level
    }

    override fun getDescription(translationManager: TranslationManager, player: Player?): String {
        return translationManager.getRaw(
            TranslationKey.REQUIREMENT_LEVEL,
            player,
            "{level}" to level.toString()
        )
    }
}

/**
 * Represents a dungeon completion requirement.
 *
 * @property dungeonId The ID of the required dungeon
 * @property completions The number of completions required
 */
data class CompletionsRequirement(
    @NotNull val dungeonId: String,
    @NotNull val completions: Int
) : DungeonRequirement() {
    override fun check(profile: PlayerProfile): Boolean {
        return profile.getDungeonCompletions(dungeonId) >= completions
    }

    override fun getDescription(translationManager: TranslationManager, player: Player?): String {
        return translationManager.getRaw(
            TranslationKey.REQUIREMENT_COMPLETIONS,
            player,
            "{dungeon}" to dungeonId,
            "{completions}" to completions.toString()
        )
    }
}

/**
 * Represents a best time requirement.
 *
 * @property dungeonId The ID of the required dungeon
 * @property timeInMillis The best time required
 */
data class BestTimeRequirement(
    @NotNull val dungeonId: String,
    @NotNull val timeInMillis: Long
) : DungeonRequirement() {
    override fun check(profile: PlayerProfile): Boolean {
        val bestTime = profile.getBestTime(dungeonId) ?: return false
        return bestTime <= timeInMillis
    }

    override fun getDescription(translationManager: TranslationManager, player: Player?): String {
        return translationManager.getRaw(
            TranslationKey.REQUIREMENT_BEST_TIME,
            player,
            "{dungeon}" to dungeonId,
            "{time}" to formatTime(timeInMillis)
        )
    }

    private fun formatTime(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}

/**
 * Represents an achievement-based dungeon requirement.
 *
 * @property achievementId The ID of the required achievement
 */
data class AchievementRequirement(
    @NotNull val achievementId: String
) : DungeonRequirement() {
    override fun check(profile: PlayerProfile): Boolean {
        return profile.achievements.contains(achievementId)
    }

    override fun getDescription(translationManager: TranslationManager, player: Player?): String {
        return translationManager.getRaw(
            TranslationKey.REQUIREMENT_ACHIEVEMENT,
            player,
            "{achievement}" to achievementId
        )
    }
}
