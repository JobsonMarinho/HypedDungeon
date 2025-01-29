package net.hypedmc.dungeon.model

import net.hypedmc.dungeon.i18n.TranslationKey
import net.hypedmc.dungeon.i18n.TranslationManager
import org.bukkit.entity.Player

/**
 * Represents the difficulty levels available for dungeons.
 * Each difficulty affects mob stats, rewards, and completion requirements.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
enum class DungeonDifficulty(private val translationKey: TranslationKey) {
    /**
     * Easy difficulty - balanced for casual players
     */
    EASY(TranslationKey.DIFFICULTY_EASY),

    /**
     * Medium difficulty - moderate challenge and rewards
     */
    MEDIUM(TranslationKey.DIFFICULTY_MEDIUM),

    /**
     * Hard difficulty - increased challenge and better rewards
     */
    HARD(TranslationKey.DIFFICULTY_HARD),

    /**
     * Elite difficulty - significant challenge with best rewards
     */
    ELITE(TranslationKey.DIFFICULTY_ELITE);

    fun getDisplayName(translationManager: TranslationManager, player: Player? = null): String {
        return translationManager.getRaw(translationKey, player)
    }
}
