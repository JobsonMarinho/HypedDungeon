package net.hypedmc.dungeon.command.impl

import com.google.inject.Inject
import net.hypedmc.dungeon.command.DungeonCommand
import net.hypedmc.dungeon.i18n.TranslationManager
import net.hypedmc.dungeon.service.DungeonManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull

/**
 * Command to leave the current dungeon.
 * Players can use this to exit a dungeon at any time.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class LeaveCommand @Inject constructor(
    @NotNull private val dungeonManager: DungeonManager
) : DungeonCommand {

    @NotNull
    override fun getName(): String = "leave"

    @NotNull
    override fun getPermission(): String = "hypeddungeon.command.leave"

    @NotNull
    override fun getUsage(): String = "/dungeon leave"

    @NotNull
    override fun getDescription(): String = "Leave the current dungeon"

    @NotNull
    override fun getAliases(): List<String> = listOf("l", "quit", "exit")

    override fun execute(
        @NotNull sender: CommandSender,
        @NotNull args: Array<String>,
        @NotNull translationManager: TranslationManager
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage(translationManager.translate("errors.player-only"))
            return true
        }

        val result = dungeonManager.leaveDungeon(sender)

        when (result) {
            DungeonManager.LeaveResult.SUCCESS -> {
                sender.sendMessage(translationManager.translate("dungeon.leave.success"))
            }
            DungeonManager.LeaveResult.NOT_IN_DUNGEON -> {
                sender.sendMessage(translationManager.translate("errors.not-in-dungeon"))
            }
        }

        return true
    }
}
