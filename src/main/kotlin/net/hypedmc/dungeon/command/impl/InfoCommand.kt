package net.hypedmc.dungeon.command.impl

import com.google.inject.Inject
import net.hypedmc.dungeon.command.DungeonCommand
import net.hypedmc.dungeon.i18n.TranslationManager
import net.hypedmc.dungeon.service.DungeonManager
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull

/**
 * Command to display detailed information about a dungeon.
 * Shows stats, requirements, and current status.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class InfoCommand @Inject constructor(
    @NotNull private val dungeonManager: DungeonManager
) : DungeonCommand, TabCompleter {

    @NotNull
    override fun getName(): String = "info"

    @NotNull
    override fun getPermission(): String = "hypeddungeon.command.info"

    @NotNull
    override fun getUsage(): String = "/dungeon info <dungeon>"

    @NotNull
    override fun getDescription(): String = "View detailed information about a dungeon"

    @NotNull
    override fun getAliases(): List<String> = listOf("i", "information", "details")

    override fun execute(
        @NotNull sender: CommandSender,
        @NotNull args: Array<String>,
        @NotNull translationManager: TranslationManager
    ): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage(translationManager.translate("errors.invalid-args")
                .replace("{usage}", getUsage()))
            return true
        }

        val dungeonId = args[0]
        val dungeon = dungeonManager.getDungeon(dungeonId)

        if (dungeon == null) {
            sender.sendMessage(translationManager.translate("errors.dungeon-not-found")
                .replace("{dungeon}", dungeonId))
            return true
        }

        // Header
        sender.sendMessage(translationManager.translate("dungeon.info.header")
            .replace("{dungeon}", dungeon.name))

        // Basic Info
        sender.sendMessage(translationManager.translate("dungeon.info.description")
            .replace("{description}", dungeon.description))
        sender.sendMessage(translationManager.translate("dungeon.info.difficulty")
            .replace("{difficulty}", translationManager.translate("difficulty.${dungeon.difficulty.name.lowercase()}")))

        // Status
        val status = dungeonManager.getDungeonStatus(dungeonId)
        val players = dungeonManager.getPlayersInDungeon(dungeonId)
        sender.sendMessage(translationManager.translate("dungeon.info.status")
            .replace("{status}", translationManager.translate("status.${status.name.lowercase()}"))
            .replace("{players}", players.size.toString())
            .replace("{max_players}", dungeonManager.getMaxPlayers(dungeonId).toString()))

        // Requirements
        sender.sendMessage(translationManager.translate("dungeon.info.requirements"))
        dungeonManager.getDungeonRequirements(dungeonId).forEach { requirement ->
            sender.sendMessage(requirement.getDescription(translationManager))
        }

        // Rewards
        sender.sendMessage(translationManager.translate("dungeon.info.rewards"))
        dungeon.rewards.forEach { (reward, chance) ->
            sender.sendMessage(translationManager.translate("dungeon.info.reward-format")
                .replace("{reward}", reward)
                .replace("{chance}", String.format("%.1f%%", chance * 100)))
        }

        // Footer
        sender.sendMessage(translationManager.translate("dungeon.info.footer"))
        return true
    }

    override fun onTabComplete(
        @NotNull sender: CommandSender,
        @NotNull command: org.bukkit.command.Command,
        @NotNull alias: String,
        @NotNull args: Array<String>
    ): List<String>? {
        if (args.size == 1) {
            val dungeons = if (sender is Player) {
                dungeonManager.getAvailableDungeons(sender)
            } else {
                dungeonManager.getAllDungeons()
            }
            return dungeons.filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }
}
