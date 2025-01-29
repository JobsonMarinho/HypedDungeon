package net.hypedmc.dungeon.mobs.listeners

import com.google.inject.Inject
import net.hypedmc.dungeon.mobs.MobManager
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.jetbrains.annotations.NotNull

/**
 * Handles events related to custom mobs in dungeons.
 * Manages mob damage, death, and special abilities.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class CustomMobListener @Inject constructor(
    @NotNull private val mobManager: MobManager
) : Listener {

    /**
     * Handles damage events for custom mobs.
     * Applies custom damage calculations and triggers special abilities.
     */
    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        val damager = event.damager

        // Mob attacking player
        if (entity is Player && damager is LivingEntity) {
            val mob = mobManager.getMob(damager)
            if (mob != null) {
                // Here we can add custom damage logic
                // For now, we'll keep the default damage
            }
        }

        // Player attacking mob
        if (entity is LivingEntity && damager is Player) {
            val mob = mobManager.getMob(entity)
            if (mob != null) {
                // Here we can add custom logic when the player attacks the mob
                // For example, special effects, sounds, etc
            }
        }
    }

    /**
     * Handles death events for custom mobs.
     * Manages loot drops and triggers death effects.
     */
    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val entity = event.entity
        val mob = mobManager.getMob(entity)
        
        if (mob != null) {
            // Remove the mob from the manager
            mobManager.removeMob(mob)

            // Here we can add custom drop logic
            // For now, we'll keep the default drops
        }
    }
}
