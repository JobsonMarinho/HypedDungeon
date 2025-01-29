package net.hypedmc.dungeon.i18n

import org.jetbrains.annotations.NotNull

/**
 * Contains all translation keys used in the plugin.
 * Each key represents a message that can be translated.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
enum class TranslationKey(@NotNull val key: String, vararg val placeholders: String) {
    // General Messages
    PREFIX("messages.prefix"),
    RELOAD_SUCCESS("messages.reload.success"),
    RELOAD_ERROR("messages.reload.error"),
    
    // Errors
    ERROR_NO_PERMISSION("errors.no-permission"),
    ERROR_PLAYER_ONLY("errors.player-only"),
    ERROR_INVALID_ARGS("errors.invalid-args"),
    ERROR_PLAYER_NOT_FOUND("errors.player-not-found", "{player}"),
    ERROR_DUNGEON_NOT_FOUND("errors.dungeon-not-found", "{dungeon}"),
    
    // Dungeon Names and Descriptions
    DUNGEON_NAME("dungeon.names.{id}"),
    DUNGEON_DESCRIPTION("dungeon.descriptions.{id}"),
    
    // Dungeon
    DUNGEON_JOIN_SUCCESS("dungeon.join.success", "{dungeon}"),
    DUNGEON_JOIN_FULL("dungeon.join.full", "{dungeon}", "{max}"),
    DUNGEON_JOIN_IN_SESSION("dungeon.join.in-session"),
    DUNGEON_LEAVE_SUCCESS("dungeon.leave.success"),
    DUNGEON_START_COUNTDOWN("dungeon.start.countdown", "{time}"),
    DUNGEON_START_BEGIN("dungeon.start.begin"),
    DUNGEON_CHECKPOINT_REACHED("dungeon.checkpoint.reached", "{checkpoint}"),
    DUNGEON_BOSS_SPAWN("dungeon.boss.spawn", "{boss}"),
    DUNGEON_COMPLETE("dungeon.complete", "{dungeon}", "{time}"),
    DUNGEON_FAIL("dungeon.fail", "{reason}"),
    
    // Status
    STATUS_WAITING("status.waiting"),
    STATUS_STARTING("status.starting"),
    STATUS_IN_PROGRESS("status.in-progress"),
    STATUS_BOSS_FIGHT("status.boss-fight"),
    STATUS_FINISHED("status.finished"),
    STATUS_CANCELLED("status.cancelled"),
    
    // Difficulties
    DIFFICULTY_EASY("difficulty.easy"),
    DIFFICULTY_MEDIUM("difficulty.medium"),
    DIFFICULTY_HARD("difficulty.hard"),
    DIFFICULTY_ELITE("difficulty.elite"),
    
    // Rewards
    REWARD_MONEY("rewards.money", "{amount}"),
    REWARD_XP("rewards.xp", "{amount}"),
    REWARD_ITEM("rewards.item", "{item}", "{amount}");

    /**
     * Gets a formatted translation key with the given arguments.
     *
     * @param args The arguments to replace the placeholders with
     * @return The formatted translation key
     */
    fun getFormatted(vararg args: Pair<String, String>): String {
        var message = key
        args.forEach { (placeholder, value) ->
            message = message.replace(placeholder, value)
        }
        return message
    }
}
