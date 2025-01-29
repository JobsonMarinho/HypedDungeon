package net.hypedmc.dungeon.mobs

import org.bukkit.entity.EntityType
import org.jetbrains.annotations.NotNull

/**
 * Defines the different types of custom mobs available in dungeons.
 * Each type has its own base stats and behavior.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
enum class CustomMobType(
    @NotNull val entityType: EntityType,
    @NotNull val nameKey: String,
    @NotNull val baseHealth: Double,
    @NotNull val baseDamage: Double,
    @NotNull val baseSpeed: Double,
    @NotNull val baseArmor: Double
) {
    /**
     * Mobs da Caverna Congelada
     */
    FROZEN_ZOMBIE(
        entityType = EntityType.ZOMBIE,
        nameKey = "mobs.frozen_cave.zombie",
        baseHealth = 30.0,
        baseDamage = 5.0,
        baseSpeed = 0.23,
        baseArmor = 2.0
    ),
    ICE_SKELETON(
        entityType = EntityType.SKELETON,
        nameKey = "mobs.frozen_cave.skeleton",
        baseHealth = 25.0,
        baseDamage = 7.0,
        baseSpeed = 0.25,
        baseArmor = 1.0
    ),
    FROST_SPIDER(
        entityType = EntityType.SPIDER,
        nameKey = "mobs.frozen_cave.spider",
        baseHealth = 20.0,
        baseDamage = 4.0,
        baseSpeed = 0.3,
        baseArmor = 0.0
    ),

    /**
     * Mobs do Templo Perdido
     */
    TEMPLE_GUARDIAN(
        entityType = EntityType.WITHER_SKELETON,
        nameKey = "mobs.lost_temple.guardian",
        baseHealth = 40.0,
        baseDamage = 8.0,
        baseSpeed = 0.2,
        baseArmor = 4.0
    ),
    CURSED_VILLAGER(
        entityType = EntityType.ZOMBIE_VILLAGER,
        nameKey = "mobs.lost_temple.cursed_villager",
        baseHealth = 35.0,
        baseDamage = 6.0,
        baseSpeed = 0.23,
        baseArmor = 2.0
    ),
    JUNGLE_CREEPER(
        entityType = EntityType.CREEPER,
        nameKey = "mobs.lost_temple.jungle_creeper",
        baseHealth = 20.0,
        baseDamage = 10.0,
        baseSpeed = 0.25,
        baseArmor = 0.0
    );

    /**
     * Gets scaled stats based on difficulty.
     *
     * @param difficulty The difficulty multiplier
     * @return The scaled stats
     */
    @NotNull
    fun getScaledStats(@NotNull difficulty: Double): CustomMobStats {
        return CustomMobStats(
            health = baseHealth * difficulty,
            damage = baseDamage * difficulty,
            speed = baseSpeed,
            armor = baseArmor * difficulty
        )
    }
}
