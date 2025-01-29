package net.hypedmc.dungeon.database

import org.jetbrains.annotations.NotNull
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

/**
 * Database table for storing player data.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
object Players : UUIDTable() {
    @NotNull val level = integer("level").default(1)
    @NotNull val experience = long("experience").default(0)
    val selectedLanguage = varchar("selected_language", 10).nullable()
    @NotNull val lastUpdated = timestamp("last_updated")
}

/**
 * Database table for storing player statistics.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
object PlayerStats : UUIDTable() {
    @NotNull val player = reference("player_id", Players)
    @NotNull val mobsKilled = integer("mobs_killed").default(0)
    @NotNull val bossesKilled = integer("bosses_killed").default(0)
    @NotNull val totalDeaths = integer("total_deaths").default(0)
    @NotNull val totalDamageDealt = double("total_damage_dealt").default(0.0)
    @NotNull val totalDamageTaken = double("total_damage_taken").default(0.0)
    @NotNull val highestDamageDealt = double("highest_damage_dealt").default(0.0)
    @NotNull val totalGoldEarned = double("total_gold_earned").default(0.0)
    @NotNull val totalTimeInDungeons = long("total_time_in_dungeons").default(0)
}

/**
 * Database table for storing dungeon completion data.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
object DungeonCompletions : Table() {
    @NotNull val player = reference("player_id", Players)
    @NotNull val dungeonId = varchar("dungeon_id", 50)
    @NotNull val completions = integer("completions").default(0)
    val bestTime = long("best_time").nullable()
    
    override val primaryKey = PrimaryKey(player, dungeonId)
}

/**
 * Database table for storing player achievements.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
object PlayerAchievements : Table() {
    @NotNull val player = reference("player_id", Players)
    @NotNull val achievementId = varchar("achievement_id", 50)
    
    override val primaryKey = PrimaryKey(player, achievementId)
}
