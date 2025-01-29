package net.hypedmc.dungeon.service

import com.google.inject.Inject
import com.google.inject.Singleton
import net.hypedmc.dungeon.model.DungeonSession
import net.hypedmc.dungeon.model.DungeonTemplate
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

@Singleton
class WorldManager @Inject constructor(
    private val plugin: JavaPlugin
) {
    companion object {
        private const val WORLD_PREFIX = "dungeon_"
    }

    fun createDungeonWorld(template: DungeonTemplate): World {
        val worldName = "${WORLD_PREFIX}${template.id}_${UUID.randomUUID()}"
        
        // Create world using MultiVerse (implement integration later)
        val world = WorldCreator(worldName)
            .environment(World.Environment.NORMAL)
            .createWorld() ?: throw IllegalStateException("Failed to create world")

        // Configure world
        world.setGameRule(org.bukkit.GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(org.bukkit.GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(org.bukkit.GameRule.DO_MOB_SPAWNING, false)
        world.time = 6000 // Set the world time to 12:00 AM
        
        return world
    }

    fun deleteDungeonWorld(world: World) {
        // Remove all players from the world
        world.players.forEach { player ->
            teleportToLobby(player)
        }

        // Unload the world
        plugin.server.unloadWorld(world, false)

        // Delete world folder
        val worldFolder = world.worldFolder
        if (worldFolder.exists() && worldFolder.isDirectory) {
            deleteDirectory(worldFolder)
        }
    }

    fun teleportToSession(player: Player, session: DungeonSession) {
        val spawnLocation = session.template.spawnPoint?.clone()
            ?: session.world.spawnLocation
        
        spawnLocation.world = session.world
        player.teleport(spawnLocation)
    }

    fun teleportToLobby(player: Player) {
        // Teleport player to the main world's spawn
        val mainWorld = plugin.server.worlds.first()
        player.teleport(mainWorld.spawnLocation)
    }

    private fun deleteDirectory(directory: File) {
        val files = directory.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                deleteDirectory(file)
            } else {
                file.delete()
            }
        }
        directory.delete()
    }
}
