package net.hypedmc.dungeon.player

import com.google.inject.Inject
import com.google.inject.Singleton
import kotlinx.coroutines.runBlocking
import net.hypedmc.dungeon.database.dao.PlayerDAO
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages player profiles and their persistence in the database.
 * Responsible for loading, saving, and managing the cache of profiles.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
@Singleton
class PlayerManager @Inject constructor(
    @NotNull private val plugin: HypedDungeon,
    @NotNull private val playerDAO: PlayerDAO
) {
    private val profileCache = ConcurrentHashMap<UUID, PlayerProfile>()

    /**
     * Initializes the player manager.
     * This method should be called during plugin initialization.
     */
    fun init() {
        // Future initialization if necessary
    }

    /**
     * Loads a player's profile from the database or cache.
     *
     * @param player The player to load the profile for
     * @return The loaded player profile
     */
    @NotNull
    fun loadProfile(@NotNull player: Player): PlayerProfile {
        return profileCache.computeIfAbsent(player.uniqueId) { uuid ->
            runBlocking { 
                playerDAO.load(uuid) ?: PlayerProfile(uuid)
            }
        }
    }

    /**
     * Retrieves a player's profile from the cache.
     *
     * @param player The player to retrieve the profile for
     * @return The player's profile, or null if not in cache
     */
    @Nullable
    fun getProfile(@NotNull player: Player): PlayerProfile? {
        return profileCache[player.uniqueId]
    }

    /**
     * Saves a player's profile to the database.
     *
     * @param player The player to save the profile for
     */
    fun saveProfile(@NotNull player: Player) {
        val profile = profileCache[player.uniqueId] ?: return
        runBlocking {
            playerDAO.save(profile)
        }
    }

    /**
     * Removes a player's profile from the cache.
     *
     * @param player The player to remove the profile for
     */
    fun unloadProfile(@NotNull player: Player) {
        profileCache.remove(player.uniqueId)
    }

    /**
     * Saves all profiles in cache to the database.
     * Useful for mass saving during server shutdown.
     */
    fun saveAllProfiles() {
        runBlocking {
            profileCache.values.forEach { profile ->
                playerDAO.save(profile)
            }
        }
    }

    /**
     * Checks if a player meets the requirements for a dungeon.
     *
     * @param player The player to check
     * @param requirements List of requirements to check
     * @return Pair containing whether all requirements were met and list of unmet requirements
     */
    @NotNull
    fun checkRequirements(
        @NotNull player: Player,
        @NotNull requirements: List<DungeonRequirement>
    ): Pair<Boolean, List<DungeonRequirement>> {
        val profile = getProfile(player) ?: return false to requirements
        
        val failedRequirements = requirements.filter { !it.check(profile) }
        return failedRequirements.isEmpty() to failedRequirements
    }

    /**
     * Updates a player's statistics after completing a dungeon.
     *
     * @param player The player to update
     * @param dungeonId ID of the completed dungeon
     * @param completionTime Completion time in milliseconds
     * @param mobsKilled Number of mobs killed
     * @param bossesKilled Number of bosses killed
     * @param damageDealt Total damage dealt
     * @param damageTaken Total damage taken
     * @param goldEarned Gold earned
     */
    fun updateStats(
        @NotNull player: Player,
        @NotNull dungeonId: String,
        @NotNull completionTime: Long,
        @NotNull mobsKilled: Int,
        @NotNull bossesKilled: Int,
        @NotNull damageDealt: Double,
        @NotNull damageTaken: Double,
        @NotNull goldEarned: Double
    ) {
        val profile = getProfile(player) ?: return
        
        // Update dungeon statistics
        profile.recordDungeonCompletion(dungeonId, completionTime)
        
        // Update general statistics
        profile.stats.apply {
            this.mobsKilled += mobsKilled
            this.bossesKilled += bossesKilled
            this.totalDamageDealt += damageDealt
            this.totalDamageTaken += damageTaken
            this.totalGoldEarned += goldEarned
            this.recordDungeonTime(completionTime)
        }
        
        // Save changes
        runBlocking {
            playerDAO.save(profile)
        }
    }

    /**
     * Removes a player's profile from the cache and database.
     *
     * @param player The player to remove the profile for
     */
    fun removeProfile(@NotNull player: Player) {
        profileCache.remove(player.uniqueId)
    }

    /**
     * Deletes a player's profile from the database.
     *
     * @param uuid The UUID of the player to delete the profile for
     */
    fun deleteProfile(@NotNull uuid: UUID) {
        profileCache.remove(uuid)
        runBlocking {
            playerDAO.delete(uuid)
        }
    }
}
