package net.hypedmc.dungeon.command.impl

import com.google.inject.Inject
import net.hypedmc.dungeon.command.DungeonCommand
import net.hypedmc.dungeon.i18n.TranslationManager
import net.hypedmc.dungeon.service.DungeonManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull

/**
 * Command to list all available dungeons.
 * Shows dungeon status, player count, and basic information.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class ListCommand @Inject constructor(
    @NotNull private val dungeonManager: DungeonManager
) : DungeonCommand {

    @NotNull
    override fun getName(): String = "list"

    @NotNull
    override fun getPermission(): String = "hypeddungeon.command.list"

    @NotNull
    override fun getUsage(): String = "/dungeon list"

    @NotNull
    override fun getDescription(): String = "List all available dungeons"

    @NotNull
    override fun getAliases(): List<String> = listOf("ls", "dungeons")

    override fun execute(
        @NotNull sender: CommandSender,
        @NotNull args: Array<String>,
        @NotNull translationManager: TranslationManager
    ): Boolean {
        sender.sendMessage(translationManager.translate("dungeon.list.header"))

        val dungeons = if (sender is Player) {
            dungeonManager.getAvailableDungeons(sender)
        } else {
            dungeonManager.getAllDungeons()
        }

        if (dungeons.isEmpty()) {
            sender.sendMessage(translationManager.translate("dungeon.list.empty"))
            return true
        }

        dungeons.forEach { dungeonId ->
            val dungeon = dungeonManager.getDungeon(dungeonId)
            if (dungeon != null) {
                val status = dungeonManager.getDungeonStatus(dungeonId)
                val players = dungeonManager.getPlayersInDungeon(dungeonId).size
                val maxPlayers = dungeonManager.getMaxPlayers(dungeonId)

                sender.sendMessage(translationManager.translate("dungeon.list.format")
                    .replace("{dungeon}", dungeon.name)
                    .replace("{description}", dungeon.description)
                    .replace("{status}", translationManager.translate("status.${status.name.lowercase()}"))
                    .replace("{players}", players.toString())
                    .replace("{max_players}", maxPlayers.toString())
                )
            }
        }

        sender.sendMessage(translationManager.translate("dungeon.list.footer"))
        return true
    }
}
