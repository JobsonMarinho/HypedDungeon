package net.hypedmc.dungeon.mobs

import org.jetbrains.annotations.NotNull

/**
 * Represents the stats of a custom mob.
 * Includes health, damage, and armor values.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
data class CustomMobStats(
    @NotNull val health: Double,
    @NotNull val damage: Double,
    @NotNull val speed: Double,
    @NotNull val armor: Double
) {
    /**
     * Scales the stats by a multiplier.
     *
     * @param multiplier The value to scale stats by
     * @return New scaled stats
     */
    @NotNull
    fun scale(@NotNull multiplier: Double): CustomMobStats {
        return CustomMobStats(
            health = health * multiplier,
            damage = damage * multiplier,
            speed = speed * multiplier,
            armor = armor * multiplier
        )
    }
}
