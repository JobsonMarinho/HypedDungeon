package net.hypedmc.dungeon.model

import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.time.Instant
import java.util.*

/**
 * Represents an active dungeon session.
 * Manages the state and players of an ongoing dungeon instance.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
data class DungeonSession(
    @NotNull val id: UUID = UUID.randomUUID(),
    @NotNull val template: DungeonTemplate,
    @NotNull val worldName: String,
    @NotNull val spawnLocation: Location,
    @NotNull val startTime: Instant = Instant.now(),
    @NotNull val players: MutableSet<UUID> = mutableSetOf(),
    @NotNull var phase: DungeonPhase = DungeonPhase.STARTING,
    @NotNull var mobsKilled: Int = 0,
    @NotNull var bossesKilled: Int = 0,
    @Nullable var completionTime: Long? = null
) {
    /**
     * Adds a player to the dungeon session.
     *
     * @param player The player to add
     * @return True if the player was added, false if they were already in the session
     */
    fun addPlayer(@NotNull player: Player): Boolean {
        return players.add(player.uniqueId)
    }

    /**
     * Removes a player from the dungeon session.
     *
     * @param player The player to remove
     * @return True if the player was removed, false if they weren't in the session
     */
    fun removePlayer(@NotNull player: Player): Boolean {
        return players.remove(player.uniqueId)
    }

    /**
     * Checks if a player is in this dungeon session.
     *
     * @param player The player to check
     * @return True if the player is in the session, false otherwise
     */
    fun hasPlayer(@NotNull player: Player): Boolean {
        return players.contains(player.uniqueId)
    }

    /**
     * Records a mob kill in the session.
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
     * Completes the dungeon session and records the completion time.
     */
    fun complete() {
        if (phase != DungeonPhase.COMPLETED) {
            phase = DungeonPhase.COMPLETED
            completionTime = Instant.now().toEpochMilli() - startTime.toEpochMilli()
        }
    }

    /**
     * Gets the elapsed time of the session in milliseconds.
     *
     * @return The elapsed time
     */
    @NotNull
    fun getElapsedTime(): Long {
        return if (completionTime != null) {
            completionTime!!
        } else {
            Instant.now().toEpochMilli() - startTime.toEpochMilli()
        }
    }

    /**
     * Gets the formatted elapsed time as a string.
     *
     * @return The formatted time string (e.g., "2m 30s")
     */
    @NotNull
    fun getFormattedElapsedTime(): String {
        val elapsedSeconds = getElapsedTime() / 1000
        val minutes = elapsedSeconds / 60
        val seconds = elapsedSeconds % 60
        return "${minutes}m ${seconds}s"
    }
}