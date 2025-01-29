package net.hypedmc.dungeon.boss.phase

import net.hypedmc.dungeon.boss.DungeonBoss
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * Represents a phase in a boss fight.
 * Each phase can have unique behavior and abilities.
 * 
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
abstract class BossPhase(
    @NotNull protected val boss: DungeonBoss,
    @NotNull val name: String,
    @NotNull val description: String
) {
    protected var isActive = false
    protected var ticks = 0

    /**
     * Called when the phase starts.
     */
    open fun start() {
        isActive = true
        ticks = 0
    }

    /**
     * Called when the phase ends.
     */
    open fun end() {
        isActive = false
    }

    /**
     * Called when the boss takes damage from a player during this phase.
     */
    open fun onDamageByPlayer(@NotNull player: Player, damage: Double) {
        // Override in specific phases
    }

    /**
     * Called when the boss takes damage from any source during this phase.
     */
    open fun onDamage(@Nullable source: Entity?, damage: Double) {
        // Override in specific phases
    }

    /**
     * Called every tick while this phase is active.
     */
    open fun tick() {
        if (isActive) {
            ticks++
        }
    }

    /**
     * Gets whether this phase is currently active.
     */
    fun isActive(): Boolean = isActive

    /**
     * Gets how many ticks this phase has been active.
     */
    fun getTicks(): Int = ticks
}
