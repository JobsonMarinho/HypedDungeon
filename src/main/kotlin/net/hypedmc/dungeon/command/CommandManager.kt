package net.hypedmc.dungeon.command

import com.google.inject.Inject
import com.google.inject.Singleton
import net.hypedmc.dungeon.HypedDungeon
import net.hypedmc.dungeon.i18n.TranslationManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.jetbrains.annotations.NotNull

/**
 * Manages all dungeon commands.
 * Handles command registration, execution, and tab completion.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
@Singleton
class CommandManager @Inject constructor(
    @NotNull private val plugin: HypedDungeon,
    @NotNull private val translationManager: TranslationManager
) : CommandExecutor, TabCompleter {

    private val commands = mutableMapOf<String, DungeonCommand>()
    private val aliases = mutableMapOf<String, DungeonCommand>()

    init {
        registerCommands()
    }

    private fun registerCommands() {
        // Register all commands here
        registerCommand(JoinCommand())
        registerCommand(LeaveCommand())
        registerCommand(ListCommand())
        registerCommand(InfoCommand())
        registerCommand(StatsCommand())
        registerCommand(AdminCommand())
    }

    private fun registerCommand(command: DungeonCommand) {
        commands[command.getName()] = command
        command.getAliases().forEach { alias ->
            aliases[alias] = command
        }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (command.name.equals("dungeon", ignoreCase = true)) {
            if (args.isEmpty()) {
                sendHelp(sender)
                return true
            }

            val subCommand = args[0].lowercase()
            val dungeonCommand = commands[subCommand] ?: aliases[subCommand]

            if (dungeonCommand == null) {
                sender.sendMessage(translationManager.translate("errors.invalid-command"))
                return true
            }

            if (!sender.hasPermission(dungeonCommand.getPermission())) {
                sender.sendMessage(translationManager.translate("errors.no-permission"))
                return true
            }

            val subArgs = args.copyOfRange(1, args.size)
            return dungeonCommand.execute(sender, subArgs, translationManager)
        }
        return false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String>? {
        if (command.name.equals("dungeon", ignoreCase = true)) {
            if (args.size == 1) {
                val available = commands.keys
                    .filter { sender.hasPermission(commands[it]!!.getPermission()) }
                return available.filter { it.startsWith(args[0].lowercase()) }
            }

            if (args.size > 1) {
                val subCommand = commands[args[0].lowercase()] ?: aliases[args[0].lowercase()]
                if (subCommand != null && sender.hasPermission(subCommand.getPermission())) {
                    when (subCommand) {
                        is TabCompleter -> {
                            return subCommand.onTabComplete(sender, command, alias, args.copyOfRange(1, args.size))
                        }
                    }
                }
            }
        }
        return emptyList()
    }

    private fun sendHelp(sender: CommandSender) {
        sender.sendMessage(translationManager.translate("help.header"))
        commands.values
            .distinct()
            .filter { sender.hasPermission(it.getPermission()) }
            .forEach { command ->
                sender.sendMessage(translationManager.translate("help.command-format")
                    .replace("{command}", command.getName())
                    .replace("{description}", command.getDescription())
                    .replace("{usage}", command.getUsage())
                )
            }
        sender.sendMessage(translationManager.translate("help.footer"))
    }
}
