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
 * Command to join a dungeon.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class JoinCommand @Inject constructor(
    @NotNull private val dungeonManager: DungeonManager
) : DungeonCommand, TabCompleter {

    override fun getName(): String = "join"

    override fun getPermission(): String = "hypeddungeon.command.join"

    override fun getUsage(): String = "/dungeon join <dungeon>"

    override fun getDescription(): String = "Join a dungeon"

    override fun getAliases(): List<String> = listOf("j", "enter")

    override fun execute(
        sender: CommandSender,
        args: Array<String>,
        translationManager: TranslationManager
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage(translationManager.translate("errors.player-only"))
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(translationManager.translate("errors.invalid-args")
                .replace("{usage}", getUsage()))
            return true
        }

        val dungeonId = args[0]
        val result = dungeonManager.joinDungeon(sender, dungeonId)

        when (result) {
            DungeonManager.JoinResult.SUCCESS -> {
                sender.sendMessage(translationManager.translate("dungeon.join.success")
                    .replace("{dungeon}", dungeonId))
            }
            DungeonManager.JoinResult.DUNGEON_NOT_FOUND -> {
                sender.sendMessage(translationManager.translate("errors.dungeon-not-found")
                    .replace("{dungeon}", dungeonId))
            }
            DungeonManager.JoinResult.ALREADY_IN_DUNGEON -> {
                sender.sendMessage(translationManager.translate("dungeon.join.in-session"))
            }
            DungeonManager.JoinResult.DUNGEON_FULL -> {
                sender.sendMessage(translationManager.translate("dungeon.join.full")
                    .replace("{dungeon}", dungeonId)
                    .replace("{max}", dungeonManager.getMaxPlayers(dungeonId).toString()))
            }
            DungeonManager.JoinResult.REQUIREMENTS_NOT_MET -> {
                sender.sendMessage(translationManager.translate("errors.requirements-not-met"))
                // Send specific requirements
                dungeonManager.getDungeonRequirements(dungeonId).forEach { requirement ->
                    sender.sendMessage(requirement.getDescription(translationManager))
                }
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: org.bukkit.command.Command,
        alias: String,
        args: Array<String>
    ): List<String>? {
        if (sender !is Player) return null
        
        if (args.size == 1) {
            return dungeonManager.getAvailableDungeons(sender)
                .filter { it.startsWith(args[0], ignoreCase = true) }
        }
        
        return emptyList()
    }
}
