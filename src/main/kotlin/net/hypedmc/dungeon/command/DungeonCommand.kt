package net.hypedmc.dungeon.command

import net.hypedmc.dungeon.i18n.TranslationManager
import org.bukkit.command.CommandSender
import org.jetbrains.annotations.NotNull

/**
 * Base interface for all dungeon commands.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
interface DungeonCommand {
    /**
     * Gets the name of the command.
     */
    @NotNull
    fun getName(): String

    /**
     * Gets the permission required to use this command.
     */
    @NotNull
    fun getPermission(): String

    /**
     * Gets the usage message for this command.
     */
    @NotNull
    fun getUsage(): String

    /**
     * Gets the description of this command.
     */
    @NotNull
    fun getDescription(): String

    /**
     * Gets the aliases for this command.
     */
    @NotNull
    fun getAliases(): List<String>

    /**
     * Executes the command.
     *
     * @param sender The command sender
     * @param args The command arguments
     * @param translationManager The translation manager for localized messages
     * @return true if the command was executed successfully, false otherwise
     */
    fun execute(
        @NotNull sender: CommandSender,
        @NotNull args: Array<String>,
        @NotNull translationManager: TranslationManager
    ): Boolean
}
