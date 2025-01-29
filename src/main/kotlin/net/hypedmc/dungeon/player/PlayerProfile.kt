package net.hypedmc.dungeon.player

import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.util.*

/**
 * Represents a player's profile in the game.
 * Contains all persistent data associated with a player including level, experience,
 * statistics, dungeon completions, and achievements.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
data class PlayerProfile(
    @NotNull val uuid: UUID,
    @NotNull var level: Int = 1,
    @NotNull var experience: Long = 0,
    @Nullable var selectedLanguage: String? = null,
    @NotNull var dungeonCompletions: MutableMap<String, Int> = mutableMapOf(),
    @NotNull var bestTimes: MutableMap<String, Long> = mutableMapOf(),
    @NotNull var stats: PlayerStats = PlayerStats(),
    @NotNull var achievements: MutableSet<String> = mutableSetOf()
) {
    /**
     * Adds experience to the player's profile and handles leveling up.
     *
     * @param amount The amount of experience to add
     * @return True if the player leveled up, false otherwise
     */
    fun addExperience(@NotNull amount: Long): Boolean {
        experience += amount
        val nextLevelExp = calculateRequiredExperience(level + 1)
        
        return if (experience >= nextLevelExp) {
            level++
            true
        } else {
            false
        }
    }

    /**
     * Records completion of a dungeon and updates best time if applicable.
     *
     * @param dungeonId The ID of the completed dungeon
     * @param completionTime The time taken to complete the dungeon in milliseconds
     */
    fun recordDungeonCompletion(@NotNull dungeonId: String, @NotNull completionTime: Long) {
        dungeonCompletions[dungeonId] = (dungeonCompletions[dungeonId] ?: 0) + 1
        
        val currentBestTime = bestTimes[dungeonId] ?: Long.MAX_VALUE
        if (completionTime < currentBestTime) {
            bestTimes[dungeonId] = completionTime
        }
    }

    /**
     * Unlocks an achievement for the player.
     *
     * @param achievementId The ID of the achievement to unlock
     * @return True if the achievement was newly unlocked, false if already unlocked
     */
    fun unlockAchievement(@NotNull achievementId: String): Boolean {
        return achievements.add(achievementId)
    }

    /**
     * Checks if the player has unlocked a specific achievement.
     *
     * @param achievementId The ID of the achievement to check
     * @return True if the achievement is unlocked, false otherwise
     */
    fun hasAchievement(@NotNull achievementId: String): Boolean {
        return achievements.contains(achievementId)
    }

    /**
     * Gets the number of times a player has completed a specific dungeon.
     *
     * @param dungeonId The ID of the dungeon to check
     * @return The number of completions
     */
    fun getDungeonCompletions(@NotNull dungeonId: String): Int {
        return dungeonCompletions[dungeonId] ?: 0
    }

    /**
     * Gets the best completion time for a specific dungeon.
     *
     * @param dungeonId The ID of the dungeon to check
     * @return The best completion time in milliseconds, or null if dungeon hasn't been completed
     */
    @Nullable
    fun getBestTime(@NotNull dungeonId: String): Long? {
        val time = bestTimes[dungeonId]
        return if (time == Long.MAX_VALUE) null else time
    }

    companion object {
        /**
         * Calculates the required experience for a given level.
         * Uses a quadratic formula to create an increasing curve.
         *
         * @param level The level to calculate required experience for
         * @return The amount of experience required
         */
        @NotNull
        fun calculateRequiredExperience(@NotNull level: Int): Long {
            return (100 * level * level + 50 * level).toLong()
        }
    }
}
