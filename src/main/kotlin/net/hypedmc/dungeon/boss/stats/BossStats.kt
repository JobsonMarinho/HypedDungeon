package net.hypedmc.dungeon.boss.stats

import org.jetbrains.annotations.NotNull

/**
 * Represents the statistics of a boss.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
data class BossStats(
    @NotNull val name: String,
    val maxHealth: Double,
    val damage: Double,
    val defense: Double,
    val speed: Double,
    val attackSpeed: Double,
    val experience: Int,
    @NotNull val abilities: List<String>,
    @NotNull val drops: Map<String, Double> // Item name to drop chance
)
