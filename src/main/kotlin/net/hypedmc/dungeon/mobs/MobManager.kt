package net.hypedmc.dungeon.mobs

import com.google.inject.Inject
import com.google.inject.Singleton
import net.hypedmc.dungeon.i18n.TranslationManager
import org.bukkit.Location
import org.bukkit.entity.LivingEntity
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.util.*

/**
 * Manages custom mobs in dungeons.
 * Handles mob spawning, tracking, and cleanup.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
@Singleton
class MobManager @Inject constructor(
    @NotNull private val translationManager: TranslationManager
) {
    private val activeMobs = mutableMapOf<UUID, CustomMob>()

    /**
     * Spawns a custom mob at the specified location.
     *
     * @param type The type of mob to spawn
     * @param location The location to spawn at
     * @param difficulty The difficulty of the mob
     * @return The spawned custom mob
     */
    @NotNull
    fun spawnMob(
        @NotNull type: CustomMobType,
        @NotNull location: Location,
        @NotNull difficulty: Double = 1.0
    ): CustomMob {
        val stats = type.getScaledStats(difficulty)
        val entity = location.world?.spawnEntity(location, type.entityType) as? LivingEntity
            ?: throw IllegalStateException("Not able to spawn ${type.entityType}")

        val mob = CustomMob(type, entity, stats, translationManager)
        activeMobs[mob.uuid] = mob
        return mob
    }

    /**
     * Gets a custom mob by its UUID.
     *
     * @param uuid The UUID of the mob to get
     * @return The custom mob, or null if not found
     */
    @Nullable
    fun getMob(@NotNull uuid: UUID): CustomMob? = activeMobs[uuid]

    /**
     * Gets a custom mob by its entity.
     *
     * @param entity The entity to get the custom mob for
     * @return The custom mob, or null if not found
     */
    @Nullable
    fun getMob(@NotNull entity: LivingEntity): CustomMob? = getMob(entity.uniqueId)

    /**
     * Removes a custom mob from tracking.
     *
     * @param uuid The UUID of the mob to remove
     */
    fun removeMob(@NotNull uuid: UUID) {
        activeMobs[uuid]?.remove()
        activeMobs.remove(uuid)
    }

    /**
     * Removes a custom mob from tracking.
     *
     * @param mob The mob to remove
     */
    fun removeMob(@NotNull mob: CustomMob) = removeMob(mob.uuid)

    /**
     * Removes all custom mobs from tracking.
     */
    fun removeAllMobs() {
        activeMobs.values.forEach { it.remove() }
        activeMobs.clear()
    }

    /**
     * Gets all active custom mobs.
     *
     * @return A collection of active custom mobs
     */
    @NotNull
    fun getActiveMobs(): Collection<CustomMob> = activeMobs.values
}
