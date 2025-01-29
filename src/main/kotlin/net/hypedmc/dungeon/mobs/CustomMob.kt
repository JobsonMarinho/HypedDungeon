package net.hypedmc.dungeon.mobs

import net.hypedmc.dungeon.i18n.TranslationManager
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.jetbrains.annotations.NotNull
import java.util.*

/**
 * Represents a custom mob in a dungeon.
 * Handles mob stats, behavior, and events.
 *
 * @author Jobs
 * @version 1.0.0
 * @since 1.0.0
 */
class CustomMob(
    @NotNull val type: CustomMobType,
    @NotNull val entity: LivingEntity,
    @NotNull val stats: CustomMobStats,
    private val translationManager: TranslationManager
) {
    /**
     * The unique ID of the mob.
     */
    val uuid: UUID = entity.uniqueId

    /**
     * The target player of the mob.
     */
    var target: Player? = null

    /**
     * The potion effects applied to the mob.
     */
    private val effects = mutableListOf<PotionEffect>()

    /**
     * The equipment of the mob.
     */
    private val equipment = mutableMapOf<EquipmentSlot, ItemStack>()

    init {
        applyStats()
        updateName()
    }

    /**
     * Applies the mob's stats to its entity.
     */
    private fun applyStats() {
        // Apply base stats
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = stats.health
        entity.health = stats.health
        entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = stats.damage
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.baseValue = stats.speed
        entity.getAttribute(Attribute.GENERIC_ARMOR)?.baseValue = stats.armor
    }

    /**
     * Updates the mob's name.
     */
    fun updateName() {
        val name = translationManager.getRaw(type.nameKey)
        entity.customName = name
        entity.isCustomNameVisible = true
    }

    /**
     * Teleports the mob to a location.
     *
     * @param location The location to teleport to
     */
    fun teleport(location: Location) {
        entity.teleport(location)
    }

    /**
     * Adds a potion effect to the mob.
     *
     * @param effect The potion effect to add
     */
    fun addPotionEffect(effect: PotionEffect) {
        effects.add(effect)
        entity.addPotionEffect(effect)
    }

    /**
     * Clears the mob's potion effects.
     */
    fun clearPotionEffects() {
        effects.forEach { entity.removePotionEffect(it.type) }
        effects.clear()
    }

    /**
     * Sets the mob's equipment.
     *
     * @param slot The equipment slot
     * @param item The item to set
     */
    fun setEquipment(slot: EquipmentSlot, item: ItemStack) {
        equipment[slot] = item
        when (slot) {
            EquipmentSlot.MAIN_HAND -> entity.equipment?.setItemInMainHand(item)
            EquipmentSlot.OFF_HAND -> entity.equipment?.setItemInOffHand(item)
            EquipmentSlot.HELMET -> entity.equipment?.helmet = item
            EquipmentSlot.CHESTPLATE -> entity.equipment?.chestplate = item
            EquipmentSlot.LEGGINGS -> entity.equipment?.leggings = item
            EquipmentSlot.BOOTS -> entity.equipment?.boots = item
        }
    }

    /**
     * Removes the mob from the world.
     */
    fun remove() {
        entity.remove()
    }

    /**
     * Checks if the mob is valid.
     *
     * @return True if the mob is valid, false otherwise
     */
    fun isValid(): Boolean = entity.isValid

    /**
     * Calculates damage after applying mob's stats.
     *
     * @param damage The base damage
     * @return The modified damage
     */
    @NotNull
    fun calculateDamage(@NotNull damage: Double): Double {
        return damage * (1.0 - (stats.armor / 100.0))
    }

    /**
     * Handles mob damage events.
     *
     * @param event The damage event
     */
    fun onDamage(@NotNull event: EntityDamageEvent) {
        // Override in subclasses for custom behavior
    }

    /**
     * Enum for equipment slots.
     */
    enum class EquipmentSlot {
        MAIN_HAND,
        OFF_HAND,
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }
}
