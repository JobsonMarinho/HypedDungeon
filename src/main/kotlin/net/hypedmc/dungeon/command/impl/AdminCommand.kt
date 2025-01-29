package net.hypedmc.dungeon.command.impl

import com.google.inject.Inject
import net.hypedmc.dungeon.command.DungeonCommand
import net.hypedmc.dungeon.i18n.TranslationManager
import net.hypedmc.dungeon.service.DungeonManager
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.jetbrains.annotations.NotNull

/**
 * Administrative command for dungeon management.
 * Allows staff to control and monitor dungeons.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class AdminCommand @Inject constructor(
    @NotNull private val dungeonManager: DungeonManager
) : DungeonCommand, TabCompleter {

    @NotNull
    override fun getName(): String = "admin"

    @NotNull
    override fun getPermission(): String = "hypeddungeon.command.admin"

    @NotNull
    override fun getUsage(): String = "/dungeon admin <reload|stop|start|reset> [dungeon]"

    @NotNull
    override fun getDescription(): String = "Manage dungeon settings and status"

    @NotNull
    override fun getAliases(): List<String> = listOf("a", "manage")

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

        when (args[0].lowercase()) {
            "reload" -> {
                dungeonManager.reloadDungeons()
                sender.sendMessage(translationManager.translate("admin.reload.success"))
            }
            "stop" -> {
                if (args.size < 2) {
                    sender.sendMessage(translationManager.translate("errors.invalid-args")
                        .replace("{usage}", "/dungeon admin stop <dungeon>"))
                    return true
                }
                
                val dungeonId = args[1]
                if (dungeonManager.stopDungeon(dungeonId)) {
                    sender.sendMessage(translationManager.translate("admin.stop.success")
                        .replace("{dungeon}", dungeonId))
                } else {
                    sender.sendMessage(translationManager.translate("errors.dungeon-not-found")
                        .replace("{dungeon}", dungeonId))
                }
            }
            "start" -> {
                if (args.size < 2) {
                    sender.sendMessage(translationManager.translate("errors.invalid-args")
                        .replace("{usage}", "/dungeon admin start <dungeon>"))
                    return true
                }
                
                val dungeonId = args[1]
                if (dungeonManager.startDungeon(dungeonId)) {
                    sender.sendMessage(translationManager.translate("admin.start.success")
                        .replace("{dungeon}", dungeonId))
                } else {
                    sender.sendMessage(translationManager.translate("errors.dungeon-not-found")
                        .replace("{dungeon}", dungeonId))
                }
            }
            "reset" -> {
                if (args.size < 2) {
                    sender.sendMessage(translationManager.translate("errors.invalid-args")
                        .replace("{usage}", "/dungeon admin reset <dungeon>"))
                    return true
                }
                
                val dungeonId = args[1]
                if (dungeonManager.resetDungeon(dungeonId)) {
                    sender.sendMessage(translationManager.translate("admin.reset.success")
                        .replace("{dungeon}", dungeonId))
                } else {
                    sender.sendMessage(translationManager.translate("errors.dungeon-not-found")
                        .replace("{dungeon}", dungeonId))
                }
            }
            else -> {
                sender.sendMessage(translationManager.translate("errors.invalid-args")
                    .replace("{usage}", getUsage()))
            }
        }

        return true
    }

    override fun onTabComplete(
        @NotNull sender: CommandSender,
        @NotNull command: org.bukkit.command.Command,
        @NotNull alias: String,
        @NotNull args: Array<String>
    ): List<String>? {
        if (!sender.hasPermission(getPermission())) {
            return emptyList()
        }

        return when (args.size) {
            1 -> listOf("reload", "stop", "start", "reset")
                .filter { it.startsWith(args[0], ignoreCase = true) }
            2 -> when (args[0].lowercase()) {
                "stop", "start", "reset" -> dungeonManager.getAllDungeons()
                    .filter { it.startsWith(args[1], ignoreCase = true) }
                else -> emptyList()
            }
            else -> emptyList()
        }
    }
}
