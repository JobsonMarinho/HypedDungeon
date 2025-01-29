package net.hypedmc.dungeon.service

import com.google.inject.Inject
import com.google.inject.Singleton
import net.hypedmc.dungeon.HypedDungeon
import net.hypedmc.dungeon.i18n.TranslationKey
import net.hypedmc.dungeon.i18n.TranslationManager
import net.hypedmc.dungeon.model.DungeonSession
import net.hypedmc.dungeon.model.DungeonTemplate
import net.hypedmc.dungeon.player.PlayerManager
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.io.File
import java.util.*

/**
 * Manages all dungeon-related operations including sessions and templates.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
@Singleton
class DungeonManager @Inject constructor(
    @NotNull private val plugin: HypedDungeon,
    @NotNull private val worldManager: WorldManager,
    @NotNull private val translationManager: TranslationManager,
    @NotNull private val playerManager: PlayerManager
) {
    private val dungeonTemplates = mutableMapOf<String, DungeonTemplate>()
    private val activeSessions = mutableMapOf<UUID, DungeonSession>()

    /**
     * Initializes the dungeon manager.
     * Loads all dungeon templates from disk.
     */
    fun init() {
        loadDungeons()
    }

    /**
     * Shuts down the dungeon manager.
     * Ends all active sessions and clears the session map.
     */
    fun shutdown() {
        activeSessions.values.forEach { it.end() }
        activeSessions.clear()
    }

    /**
     * Obtains a dungeon template by ID.
     *
     * @param id The ID of the template
     * @return The template or null if not found
     */
    @Nullable
    fun getTemplate(@NotNull id: String): DungeonTemplate? {
        return dungeonTemplates[id]
    }

    /**
     * Adds a player to a dungeon session.
     *
     * @param player The player to add
     * @param dungeonId The ID of the dungeon
     * @return True if the player was added successfully
     */
    fun joinSession(@NotNull player: Player, @NotNull dungeonId: String): Boolean {
        val template = getTemplate(dungeonId) ?: run {
            player.sendMessage(translationManager.get(TranslationKey.ERROR_DUNGEON_NOT_FOUND, player))
            return false
        }

        // Check requirements
        if (!playerManager.checkRequirements(player, template.requirements)) {
            player.sendMessage(translationManager.get(TranslationKey.ERROR_REQUIREMENTS_NOT_MET, player))
            return false
        }

        // Find or create session
        val session = findAvailableSession(dungeonId) ?: createSession(template)

        // Add player to session
        session.addPlayer(player)
        player.teleport(session.getSpawnLocation())

        return true
    }

    /**
     * Removes a player from their current dungeon session.
     *
     * @param player The player to remove
     * @return True if the player was removed successfully
     */
    fun leaveSession(@NotNull player: Player): Boolean {
        val session = getPlayerSession(player) ?: return false
        session.removePlayer(player)
        
        // Teleport back to spawn
        player.teleport(plugin.server.worlds[0].spawnLocation)

        // End session if empty
        if (session.isEmpty()) {
            session.end()
            activeSessions.remove(session.id)
        }

        return true
    }

    /**
     * Obtains the current dungeon session of a player.
     *
     * @param player The player
     * @return The session of the player or null if not in a session
     */
    @Nullable
    fun getPlayerSession(@NotNull player: Player): DungeonSession? {
        return activeSessions.values.find { it.hasPlayer(player) }
    }

    /**
     * Finds an available dungeon session.
     *
     * @param dungeonId The ID of the dungeon
     * @return The available session or null if not found
     */
    @Nullable
    private fun findAvailableSession(@NotNull dungeonId: String): DungeonSession? {
        return activeSessions.values.find { 
            it.template.id == dungeonId && !it.isFull() && !it.hasStarted()
        }
    }

    /**
     * Creates a new dungeon session.
     *
     * @param template The template of the dungeon
     * @return The created session
     */
    @NotNull
    private fun createSession(@NotNull template: DungeonTemplate): DungeonSession {
        val session = DungeonSession(
            id = UUID.randomUUID(),
            template = template,
            world = worldManager.createDungeonWorld(template)
        )
        activeSessions[session.id] = session
        return session
    }

    /**
     * Loads all dungeon templates from disk.
     */
    private fun loadDungeons() {
        val file = File(plugin.dataFolder, "dungeons.yml")
        if (!file.exists()) {
            plugin.saveResource("dungeons.yml", false)
        }

        val config = YamlConfiguration.loadConfiguration(file)
        config.getConfigurationSection("dungeons")?.getKeys(false)?.forEach { id ->
            val section = config.getConfigurationSection("dungeons.$id") ?: return@forEach
            try {
                val template = DungeonTemplate.fromConfig(id, section)
                dungeonTemplates[id] = template
                plugin.logger.info("Loaded dungeon template: $id")
            } catch (e: Exception) {
                plugin.logger.severe("Failed to load dungeon template $id: ${e.message}")
            }
        }
    }
}
