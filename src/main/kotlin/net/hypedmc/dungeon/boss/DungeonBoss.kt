package net.hypedmc.dungeon.boss

import net.hypedmc.dungeon.boss.phase.BossPhase
import net.hypedmc.dungeon.boss.stats.BossStats
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull
import java.util.*

/**
 * Base class for dungeon bosses.
 * Handles boss phases, stats, and behavior.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
abstract class DungeonBoss(
    @NotNull val entity: LivingEntity,
    @NotNull val stats: BossStats
) {
    private val phases = mutableListOf<BossPhase>()
    private var currentPhaseIndex = 0
    private var currentPhase: BossPhase? = null
    
    val uuid: UUID = entity.uniqueId
    var isDead = false
        private set

    init {
        setupPhases()
        startFirstPhase()
    }

    /**
     * Sets up the boss phases.
     * Should be implemented by each boss to define their phases.
     */
    protected abstract fun setupPhases()

    /**
     * Adds a phase to the boss fight.
     */
    protected fun addPhase(@NotNull phase: BossPhase) {
        phases.add(phase)
    }

    /**
     * Starts the first phase of the boss fight.
     */
    private fun startFirstPhase() {
        if (phases.isNotEmpty()) {
            currentPhase = phases[0]
            currentPhase?.start()
        }
    }

    /**
     * Advances to the next phase.
     * @return true if there was a next phase, false if this was the last phase
     */
    fun nextPhase(): Boolean {
        currentPhase?.end()
        
        if (currentPhaseIndex + 1 >= phases.size) {
            return false
        }

        currentPhaseIndex++
        currentPhase = phases[currentPhaseIndex]
        currentPhase?.start()
        return true
    }

    /**
     * Called when the boss takes damage from a player.
     */
    open fun onDamageByPlayer(@NotNull player: Player, damage: Double) {
        currentPhase?.onDamageByPlayer(player, damage)
        
        // Check if we should transition to next phase
        if (entity.health <= entity.maxHealth * (1.0 - ((currentPhaseIndex + 1.0) / phases.size))) {
            if (!nextPhase()) {
                // No more phases, boss fight is over
                onDeath()
            }
        }
    }

    /**
     * Called when the boss takes damage from any source.
     */
    open fun onDamage(@NotNull source: Entity?, damage: Double) {
        currentPhase?.onDamage(source, damage)
    }

    /**
     * Called when the boss dies.
     */
    open fun onDeath() {
        if (!isDead) {
            isDead = true
            currentPhase?.end()
        }
    }

    /**
     * Called every tick to update the boss.
     */
    open fun tick() {
        if (!isDead) {
            currentPhase?.tick()
        }
    }

    /**
     * Gets the current phase of the boss.
     */
    fun getCurrentPhase(): BossPhase? = currentPhase

    /**
     * Gets the location of the boss.
     */
    fun getLocation(): Location = entity.location

    /**
     * Gets the current health of the boss.
     */
    fun getHealth(): Double = entity.health

    /**
     * Gets the maximum health of the boss.
     */
    fun getMaxHealth(): Double = entity.maxHealth
}
