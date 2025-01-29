package net.hypedmc.dungeon.player

import org.jetbrains.annotations.NotNull

/**
 * Represents a player's statistics in the game.
 * Tracks various metrics like kills, deaths, damage dealt/taken, and time spent in dungeons.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
data class PlayerStats(
    @NotNull var mobsKilled: Int = 0,
    @NotNull var bossesKilled: Int = 0,
    @NotNull var totalDeaths: Int = 0,
    @NotNull var totalDamageDealt: Double = 0.0,
    @NotNull var totalDamageTaken: Double = 0.0,
    @NotNull var highestDamageDealt: Double = 0.0,
    @NotNull var totalGoldEarned: Double = 0.0,
    @NotNull var totalTimeInDungeons: Long = 0
) {
    /**
     * Records a mob kill and updates relevant statistics.
     *
     * @param isBoss Whether the killed mob was a boss
     */
    fun recordKill(@NotNull isBoss: Boolean) {
        if (isBoss) {
            bossesKilled++
        } else {
            mobsKilled++
        }
    }

    /**
     * Records damage dealt by the player and updates highest damage if applicable.
     *
     * @param amount The amount of damage dealt
     */
    fun recordDamageDealt(@NotNull amount: Double) {
        totalDamageDealt += amount
        if (amount > highestDamageDealt) {
            highestDamageDealt = amount
        }
    }

    /**
     * Records damage taken by the player.
     *
     * @param amount The amount of damage taken
     */
    fun recordDamageTaken(@NotNull amount: Double) {
        totalDamageTaken += amount
    }

    /**
     * Records a player death.
     */
    fun recordDeath() {
        totalDeaths++
    }

    /**
     * Records gold earned by the player.
     *
     * @param amount The amount of gold earned
     */
    fun recordGoldEarned(@NotNull amount: Double) {
        totalGoldEarned += amount
    }

    /**
     * Records time spent in a dungeon.
     *
     * @param timeInMillis The time spent in milliseconds
     */
    fun recordDungeonTime(@NotNull timeInMillis: Long) {
        totalTimeInDungeons += timeInMillis
    }

    /**
     * Gets the kill/death ratio of the player.
     * Returns 0.0 if the player has no deaths.
     *
     * @return The kill/death ratio
     */
    @NotNull
    fun getKDRatio(): Double {
        return if (totalDeaths > 0) {
            (mobsKilled + bossesKilled).toDouble() / totalDeaths
        } else {
            0.0
        }
    }

    /**
     * Gets the average damage per kill.
     * Returns 0.0 if the player has no kills.
     *
     * @return The average damage per kill
     */
    @NotNull
    fun getAverageDamagePerKill(): Double {
        val totalKills = mobsKilled + bossesKilled
        return if (totalKills > 0) {
            totalDamageDealt / totalKills
        } else {
            0.0
        }
    }

    /**
     * Gets the total time spent in dungeons in a human-readable format.
     *
     * @return A formatted string representing the time spent (e.g., "2h 30m 15s")
     */
    @NotNull
    fun getFormattedTotalTime(): String {
        val seconds = totalTimeInDungeons / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            append("${remainingSeconds}s")
        }.trim()
    }
}
