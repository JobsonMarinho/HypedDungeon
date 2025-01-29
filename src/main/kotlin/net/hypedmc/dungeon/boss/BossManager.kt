package net.hypedmc.dungeon.boss

import com.google.inject.Inject
import com.google.inject.Singleton
import net.hypedmc.dungeon.HypedDungeon
import net.hypedmc.dungeon.boss.impl.FrozenKing
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.scheduler.BukkitRunnable
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.util.*

/**
 * Manages all boss entities in the game.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
@Singleton
class BossManager @Inject constructor(
    @NotNull private val plugin: HypedDungeon
) {
    private val activeBosses = mutableMapOf<UUID, DungeonBoss>()

    init {
        // Start boss tick task
        object : BukkitRunnable() {
            override fun run() {
                tickBosses()
            }
        }.runTaskTimer(plugin, 1L, 1L)
    }

    /**
     * Spawns a boss at the specified location.
     *
     * @param type The type of boss to spawn
     * @param location The location to spawn the boss
     * @return The spawned boss instance
     */
    @NotNull
    fun spawnBoss(@NotNull type: BossType, @NotNull location: Location): DungeonBoss {
        val entity = when (type) {
            BossType.FROZEN_KING -> {
                location.world.spawnEntity(location, EntityType.ZOMBIE) as LivingEntity
            }
            // Add more boss types here
        }

        val boss = when (type) {
            BossType.FROZEN_KING -> FrozenKing(entity)
            // Add more boss types here
        }

        activeBosses[entity.uniqueId] = boss
        return boss
    }

    /**
     * Gets a boss by its entity UUID.
     *
     * @param uuid The UUID of the boss entity
     * @return The boss instance or null if not found
     */
    @Nullable
    fun getBoss(@NotNull uuid: UUID): DungeonBoss? {
        return activeBosses[uuid]
    }

    /**
     * Removes a boss from the game.
     *
     * @param uuid The UUID of the boss entity
     */
    fun removeBoss(@NotNull uuid: UUID) {
        activeBosses.remove(uuid)
    }

    /**
     * Gets all active bosses.
     *
     * @return List of all active bosses
     */
    @NotNull
    fun getActiveBosses(): List<DungeonBoss> {
        return activeBosses.values.toList()
    }

    private fun tickBosses() {
        activeBosses.values.forEach { boss ->
            try {
                boss.tick()
            } catch (e: Exception) {
                plugin.logger.severe("Error ticking boss ${boss.uuid}: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
