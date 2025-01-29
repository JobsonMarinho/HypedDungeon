package net.hypedmc.dungeon.boss.impl

import net.hypedmc.dungeon.boss.DungeonBoss
import net.hypedmc.dungeon.boss.phase.BossPhase
import net.hypedmc.dungeon.boss.stats.BossStats
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jetbrains.annotations.NotNull

/**
 * The Frozen King boss.
 * A powerful ice-themed boss with multiple phases.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class FrozenKing(entity: LivingEntity) : DungeonBoss(
    entity,
    BossStats(
        name = "Frozen King",
        maxHealth = 1000.0,
        damage = 15.0,
        defense = 10.0,
        speed = 0.3,
        attackSpeed = 1.0,
        experience = 500,
        abilities = listOf("Ice Storm", "Frost Nova", "Summon Minions"),
        drops = mapOf(
            "DIAMOND" to 0.8,
            "EMERALD" to 0.5,
            "FROZEN_SWORD" to 0.3
        )
    )
) {
    override fun setupPhases() {
        // Phase 1: Normal combat
        addPhase(object : BossPhase(this, "Normal", "The Frozen King fights normally") {
            override fun start() {
                super.start()
                entity.world.playSound(entity.location, Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f)
            }

            override fun tick() {
                super.tick()
                if (ticks % 20 == 0) {
                    // Every second, spawn ice particles
                    entity.world.spawnParticle(
                        Particle.SNOWFLAKE,
                        entity.location.add(0.0, 1.0, 0.0),
                        10,
                        0.5,
                        0.5,
                        0.5,
                        0.0
                    )
                }
            }
        })

        // Phase 2: Ice Storm
        addPhase(object : BossPhase(this, "Ice Storm", "The Frozen King unleashes an ice storm") {
            override fun start() {
                super.start()
                entity.world.playSound(entity.location, Sound.BLOCK_GLASS_BREAK, 1.0f, 0.5f)
            }

            override fun tick() {
                super.tick()
                if (ticks % 40 == 0) {
                    // Every 2 seconds, create ice storm effect
                    val loc = entity.location
                    for (i in 0..360 step 20) {
                        val angle = Math.toRadians(i.toDouble())
                        val x = loc.x + Math.cos(angle) * 3
                        val z = loc.z + Math.sin(angle) * 3
                        loc.world.spawnParticle(
                            Particle.SNOWBALL,
                            x,
                            loc.y + 1,
                            z,
                            5,
                            0.2,
                            0.2,
                            0.2,
                            0.0
                        )
                    }
                }
            }

            override fun onDamageByPlayer(@NotNull player: Player, damage: Double) {
                // 20% chance to freeze player
                if (Math.random() < 0.2) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 60, 2))
                }
            }
        })

        // Phase 3: Minion Phase
        addPhase(object : BossPhase(this, "Minions", "The Frozen King summons his minions") {
            override fun start() {
                super.start()
                // Spawn minions
                val loc = entity.location
                for (i in 1..4) {
                    val minion = loc.world.spawnEntity(
                        getSpawnLocation(loc, i),
                        EntityType.ZOMBIE
                    ) as LivingEntity
                    
                    minion.customName = "§bFrozen Minion"
                    minion.isCustomNameVisible = true
                    minion.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 1))
                }
            }

            private fun getSpawnLocation(center: Location, index: Int): Location {
                val angle = Math.toRadians((index * 90).toDouble())
                return center.clone().add(
                    Math.cos(angle) * 3,
                    0.0,
                    Math.sin(angle) * 3
                )
            }

            override fun tick() {
                super.tick()
                if (ticks % 100 == 0) {
                    // Every 5 seconds, heal minions
                    entity.world.getNearbyEntities(entity.location, 10.0, 10.0, 10.0)
                        .filterIsInstance<LivingEntity>()
                        .filter { it.customName?.contains("Frozen Minion") == true }
                        .forEach { 
                            it.health = Math.min(it.health + 5, it.maxHealth)
                            it.world.spawnParticle(
                                Particle.HEART,
                                it.location.add(0.0, 1.0, 0.0),
                                3,
                                0.2,
                                0.2,
                                0.2,
                                0.0
                            )
                        }
                }
            }
        })
    }

    override fun onDeath() {
        super.onDeath()
        // Create a dramatic death effect
        val loc = entity.location
        loc.world.strikeLightningEffect(loc)
        loc.world.playSound(loc, Sound.ENTITY_WITHER_DEATH, 1.0f, 0.5f)
        loc.world.spawnParticle(
            Particle.EXPLOSION_HUGE,
            loc,
            10,
            1.0,
            1.0,
            1.0,
            0.0
        )
    }
}
