package net.hypedmc.dungeon.command.impl

import com.google.inject.Inject
import net.hypedmc.dungeon.command.DungeonCommand
import net.hypedmc.dungeon.i18n.TranslationManager
import net.hypedmc.dungeon.player.PlayerManager
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull

/**
 * Command to view player dungeon statistics.
 * Shows completion rates, kills, deaths, and other stats.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class StatsCommand @Inject constructor(
    @NotNull private val playerManager: PlayerManager
) : DungeonCommand, TabCompleter {

    @NotNull
    override fun getName(): String = "stats"

    @NotNull
    override fun getPermission(): String = "hypeddungeon.command.stats"

    @NotNull
    override fun getUsage(): String = "/dungeon stats [player]"

    @NotNull
    override fun getDescription(): String = "View dungeon statistics"

    @NotNull
    override fun getAliases(): List<String> = listOf("statistics", "stat")

    override fun execute(
        @NotNull sender: CommandSender,
        @NotNull args: Array<String>,
        @NotNull translationManager: TranslationManager
    ): Boolean {
        val target = if (args.isEmpty()) {
            if (sender !is Player) {
                sender.sendMessage(translationManager.translate("errors.player-only"))
                return true
            }
            sender
        } else {
            if (!sender.hasPermission("hypeddungeon.command.stats.others")) {
                sender.sendMessage(translationManager.translate("errors.no-permission"))
                return true
            }
            Bukkit.getPlayer(args[0])
        }

        if (target == null) {
            sender.sendMessage(translationManager.translate("errors.player-not-found")
                .replace("{player}", args[0]))
            return true
        }

        val stats = playerManager.getPlayerStats(target)

        // Header
        sender.sendMessage(translationManager.translate("player.stats.title")
            .replace("{player}", target.name))

        // General Stats
        sender.sendMessage(translationManager.translate("player.stats.mobs_killed")
            .replace("{amount}", stats.mobsKilled.toString()))
        sender.sendMessage(translationManager.translate("player.stats.bosses_killed")
            .replace("{amount}", stats.bossesKilled.toString()))
        sender.sendMessage(translationManager.translate("player.stats.deaths")
            .replace("{amount}", stats.deaths.toString()))
        sender.sendMessage(translationManager.translate("player.stats.damage_dealt")
            .replace("{amount}", String.format("%.1f", stats.damageDealt)))
        sender.sendMessage(translationManager.translate("player.stats.damage_taken")
            .replace("{amount}", String.format("%.1f", stats.damageTaken)))
        sender.sendMessage(translationManager.translate("player.stats.highest_damage")
            .replace("{amount}", String.format("%.1f", stats.highestDamage)))
        sender.sendMessage(translationManager.translate("player.stats.gold_earned")
            .replace("{amount}", stats.goldEarned.toString()))
        sender.sendMessage(translationManager.translate("player.stats.time_in_dungeons")
            .replace("{time}", formatTime(stats.timeInDungeons)))

        return true
    }

    override fun onTabComplete(
        @NotNull sender: CommandSender,
        @NotNull command: org.bukkit.command.Command,
        @NotNull alias: String,
        @NotNull args: Array<String>
    ): List<String>? {
        if (args.size == 1 && sender.hasPermission("hypeddungeon.command.stats.others")) {
            return Bukkit.getOnlinePlayers()
                .map { it.name }
                .filter { it.startsWith(args[0], ignoreCase = true) }
        }
        return emptyList()
    }

    private fun formatTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
}
