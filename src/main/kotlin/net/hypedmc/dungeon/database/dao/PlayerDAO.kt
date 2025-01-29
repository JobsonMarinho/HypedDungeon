package net.hypedmc.dungeon.database.dao

import kotlinx.coroutines.Dispatchers
import net.hypedmc.dungeon.database.*
import net.hypedmc.dungeon.player.PlayerProfile
import net.hypedmc.dungeon.player.PlayerStats
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant
import java.util.*

/**
 * Data Access Object for managing player data in the database.
 * Handles CRUD operations for player profiles, statistics, and achievements.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class PlayerDAO {
    /**
     * Saves or updates a player profile in the database.
     * This includes basic profile data, statistics, dungeon completions, and achievements.
     *
     * @param profile The player profile to save
     */
    suspend fun save(@NotNull profile: PlayerProfile) = newSuspendedTransaction(Dispatchers.IO) {
        // Save basic player data
        Players.upsert(Players.id) {
            it[id] = profile.uuid
            it[level] = profile.level
            it[experience] = profile.experience
            it[selectedLanguage] = profile.selectedLanguage
            it[lastUpdated] = Instant.now()
        }

        // Save statistics
        PlayerStats.upsert(PlayerStats.player) {
            it[player] = profile.uuid
            it[mobsKilled] = profile.stats.mobsKilled
            it[bossesKilled] = profile.stats.bossesKilled
            it[totalDeaths] = profile.stats.totalDeaths
            it[totalDamageDealt] = profile.stats.totalDamageDealt
            it[totalDamageTaken] = profile.stats.totalDamageTaken
            it[highestDamageDealt] = profile.stats.highestDamageDealt
            it[totalGoldEarned] = profile.stats.totalGoldEarned
            it[totalTimeInDungeons] = profile.stats.totalTimeInDungeons
        }

        // Save dungeon completions
        profile.dungeonCompletions.forEach { (dungeonId, completions) ->
            DungeonCompletions.upsert(DungeonCompletions.primaryKey!!) {
                it[player] = profile.uuid
                it[DungeonCompletions.dungeonId] = dungeonId
                it[DungeonCompletions.completions] = completions
                it[bestTime] = profile.bestTimes[dungeonId]
            }
        }

        // Save achievements
        PlayerAchievements.deleteWhere { PlayerAchievements.player eq profile.uuid }
        profile.achievements.forEach { achievementId ->
            PlayerAchievements.insert {
                it[player] = profile.uuid
                it[PlayerAchievements.achievementId] = achievementId
            }
        }
    }

    /**
     * Loads a player profile from the database.
     *
     * @param uuid The UUID of the player to load
     * @return The loaded player profile, or null if not found
     */
    @Nullable
    suspend fun load(@NotNull uuid: UUID): PlayerProfile? = newSuspendedTransaction(Dispatchers.IO) {
        val playerRow = Players.select { Players.id eq uuid }.singleOrNull() ?: return@newSuspendedTransaction null

        // Load statistics
        val statsRow = PlayerStats.select { PlayerStats.player eq uuid }.singleOrNull()
        val stats = if (statsRow != null) {
            PlayerStats(
                mobsKilled = statsRow[PlayerStats.mobsKilled],
                bossesKilled = statsRow[PlayerStats.bossesKilled],
                totalDeaths = statsRow[PlayerStats.totalDeaths],
                totalDamageDealt = statsRow[PlayerStats.totalDamageDealt],
                totalDamageTaken = statsRow[PlayerStats.totalDamageTaken],
                highestDamageDealt = statsRow[PlayerStats.highestDamageDealt],
                totalGoldEarned = statsRow[PlayerStats.totalGoldEarned],
                totalTimeInDungeons = statsRow[PlayerStats.totalTimeInDungeons]
            )
        } else {
            PlayerStats()
        }

        // Load dungeon completions
        val dungeonCompletions = DungeonCompletions
            .select { DungeonCompletions.player eq uuid }
            .associate {
                it[DungeonCompletions.dungeonId] to it[DungeonCompletions.completions]
            }.toMutableMap()

        // Load best times
        val bestTimes = DungeonCompletions
            .select { DungeonCompletions.player eq uuid }
            .associate {
                it[DungeonCompletions.dungeonId] to (it[DungeonCompletions.bestTime] ?: Long.MAX_VALUE)
            }.toMutableMap()

        // Load achievements
        val achievements = PlayerAchievements
            .select { PlayerAchievements.player eq uuid }
            .map { it[PlayerAchievements.achievementId] }
            .toMutableSet()

        return@newSuspendedTransaction PlayerProfile(
            uuid = uuid,
            level = playerRow[Players.level],
            experience = playerRow[Players.experience],
            selectedLanguage = playerRow[Players.selectedLanguage],
            dungeonCompletions = dungeonCompletions,
            bestTimes = bestTimes,
            stats = stats,
            achievements = achievements
        )
    }

    /**
     * Deletes a player profile from the database.
     * This will remove all associated data including statistics, completions, and achievements.
     *
     * @param uuid The UUID of the player to delete
     */
    suspend fun delete(@NotNull uuid: UUID) = newSuspendedTransaction(Dispatchers.IO) {
        // Delete in cascade order
        PlayerAchievements.deleteWhere { player eq uuid }
        DungeonCompletions.deleteWhere { player eq uuid }
        PlayerStats.deleteWhere { player eq uuid }
        Players.deleteWhere { id eq uuid }
    }
}
