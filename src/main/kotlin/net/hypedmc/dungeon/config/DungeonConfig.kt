package net.hypedmc.dungeon.config

import com.google.inject.Inject
import com.google.inject.Singleton
import net.hypedmc.dungeon.model.DungeonDifficulty
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * Configuration for dungeon settings.
 * Manages loading and saving of dungeon data.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
@Singleton
class DungeonConfig @Inject constructor(
    @NotNull private val plugin: JavaPlugin
) : Configuration(plugin, "dungeons.yml") {

    private var dungeons: Map<String, DungeonConfigData> = emptyMap()

    /**
     * Loads all dungeon settings from the configuration.
     */
    override fun load() {
        dungeons = manager.getSection("dungeons")?.getKeys(false)?.associateWith { id ->
            val section = manager.getSection("dungeons.$id") ?: return@associateWith DungeonConfigData()
            
            DungeonConfigData(
                name = section.getString("name") ?: id,
                description = section.getString("description") ?: "",
                difficulty = DungeonDifficulty.valueOf(section.getString("difficulty") ?: "EASY"),
                minLevel = section.getInt("min-level", 1),
                maxPlayers = section.getInt("max-players", 4),
                minPlayers = section.getInt("min-players", 1),
                spawnPoint = section.getLocation("spawn-point"),
                bossSpawnPoints = section.getSection("boss-spawn-points")?.getKeys(false)?.associateWith { bossId ->
                    section.getLocation("boss-spawn-points.$bossId")
                }?.filterValues { it != null } as? Map<String, Location> ?: emptyMap(),
                mobSpawnPoints = section.getLocationList("mob-spawn-points"),
                checkpoints = section.getSection("checkpoints")?.getKeys(false)?.associateWith { checkpointId ->
                    section.getLocation("checkpoints.$checkpointId")
                }?.filterValues { it != null } as? Map<String, Location> ?: emptyMap(),
                requirements = section.getStringList("requirements"),
                rewards = section.getSection("rewards")?.getKeys(false)?.associateWith { rewardId ->
                    section.getDouble("rewards.$rewardId")
                } ?: emptyMap()
            )
        } ?: emptyMap()
    }

    /**
     * Saves all dungeon settings to the configuration.
     */
    override fun save() {
        dungeons.forEach { (id, data) ->
            val path = "dungeons.$id"
            manager.set("$path.name", data.name)
            manager.set("$path.description", data.description)
            manager.set("$path.difficulty", data.difficulty.name)
            manager.set("$path.min-level", data.minLevel)
            manager.set("$path.max-players", data.maxPlayers)
            manager.set("$path.min-players", data.minPlayers)
            manager.set("$path.spawn-point", data.spawnPoint)
            
            data.bossSpawnPoints.forEach { (bossId, location) ->
                manager.set("$path.boss-spawn-points.$bossId", location)
            }
            
            manager.set("$path.mob-spawn-points", data.mobSpawnPoints)
            
            data.checkpoints.forEach { (checkpointId, location) ->
                manager.set("$path.checkpoints.$checkpointId", location)
            }
            
            manager.set("$path.requirements", data.requirements)
            
            data.rewards.forEach { (rewardId, amount) ->
                manager.set("$path.rewards.$rewardId", amount)
            }
        }
        manager.save()
    }

    /**
     * Gets a dungeon setting by its ID.
     *
     * @param id The ID of the dungeon
     * @return The dungeon setting or null if not found
     */
    @Nullable
    fun getDungeonData(@NotNull id: String): DungeonConfigData? = dungeons[id]
    
    /**
     * Gets all dungeon settings.
     *
     * @return Map of all dungeon settings
     */
    @NotNull
    fun getAllDungeons(): Map<String, DungeonConfigData> = dungeons

    /**
     * Data class for dungeon settings.
     */
    data class DungeonConfigData(
        val name: String = "",
        val description: String = "",
        val difficulty: DungeonDifficulty = DungeonDifficulty.EASY,
        val minLevel: Int = 1,
        val maxPlayers: Int = 4,
        val minPlayers: Int = 1,
        val spawnPoint: Location? = null,
        val bossSpawnPoints: Map<String, Location> = emptyMap(),
        val mobSpawnPoints: List<Location> = emptyList(),
        val checkpoints: Map<String, Location> = emptyMap(),
        val requirements: List<String> = emptyList(),
        val rewards: Map<String, Double> = emptyMap()
    )
}
