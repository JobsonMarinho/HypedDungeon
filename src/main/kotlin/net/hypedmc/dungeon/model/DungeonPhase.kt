package net.hypedmc.dungeon.model

/**
 * Represents the different phases of a dungeon session.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
enum class DungeonPhase {
    /**
     * Players are waiting for the session to start
     */
    WAITING,

    /**
     * The dungeon session is starting
     */
    STARTING,

    /**
     * The dungeon session is in progress
     */
    IN_PROGRESS,

    /**
     * The boss fight has begun
     */
    BOSS_FIGHT,

    /**
     * The dungeon has been completed successfully
     */
    FINISHED,

    /**
     * The dungeon has been cancelled
     */
    CANCELLED
}
