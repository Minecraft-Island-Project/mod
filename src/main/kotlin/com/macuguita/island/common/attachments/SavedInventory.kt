/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

@file:Suppress("UnstableApiUsage")

package com.macuguita.island.common.attachments

import com.macuguita.island.common.Island.id
import com.macuguita.island.mixin.InventoryAccessor
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.minecraft.world.entity.EntityEquipment
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack

object SavedInventory {

    val ATTACHMENT: AttachmentType<SavedInventoryAttachedData> =
        AttachmentRegistry.create(
            id("saved_inventory")
        ) { builder ->
            builder
                .initializer { SavedInventoryAttachedData.DEFAULT }
                .persistent(SavedInventoryAttachedData.CODEC)
                .copyOnDeath()
        }

    operator fun get(target: AttachmentTarget): SavedInventoryData =
        SavedInventoryData(target)
}

data class SavedInventoryAttachedData(
    val playerInventory: List<Pair<Int, ItemStack>>,
    val equipmentInventory: EntityEquipment,
) {
    companion object {
        val DEFAULT = SavedInventoryAttachedData(emptyList(), EntityEquipment())

        val CODEC: Codec<SavedInventoryAttachedData> = RecordCodecBuilder.create { instance ->
            val pairCodec: Codec<Pair<Int, ItemStack>> = RecordCodecBuilder.create { pairInstance ->
                pairInstance.group(
                    Codec.INT.fieldOf("slot").forGetter { it.first },
                    ItemStack.CODEC.fieldOf("stack").forGetter { it.second }
                ).apply(pairInstance) { slot, stack -> slot to stack }
            }

            instance.group(
                pairCodec.listOf().fieldOf("playerInventory").forGetter { it.playerInventory },
                EntityEquipment.CODEC.fieldOf("equipment").forGetter { it.equipmentInventory },
            ).apply(instance, ::SavedInventoryAttachedData)
        }
    }

}

data class SavedInventoryData(private val target: AttachmentTarget) {

    private fun current(): SavedInventoryAttachedData =
        target.getAttachedOrElse(SavedInventory.ATTACHMENT, SavedInventoryAttachedData.DEFAULT)

    fun hasSavedInventory(): Boolean {
        val data = current()
        return data.playerInventory.isNotEmpty() || !data.equipmentInventory.isEmpty
    }

    fun saveInventory(inventory: Inventory) {
        val items = inventory.nonEquipmentItems.mapIndexedNotNull { slot, stack ->
            if (stack.isEmpty) null else slot to stack.copy()
        }

        val equipment = (inventory as InventoryAccessor).`island$getEquipment`()
        val equipmentCopy = EntityEquipment()
        equipmentCopy.setAll(equipment)

        target.setAttached(SavedInventory.ATTACHMENT, SavedInventoryAttachedData(items, equipmentCopy))
    }

    fun loadInventory(inventory: Inventory) {
        inventory.clearContent()
        current().playerInventory.forEach { (slot, stack) ->
            inventory.setItem(slot, stack.copy())
        }
        (inventory as InventoryAccessor).`island$getEquipment`().setAll(current().equipmentInventory)
        clearSavedInventory()
    }

    fun clearSavedInventory() {
        target.setAttached(SavedInventory.ATTACHMENT, SavedInventoryAttachedData.DEFAULT)
    }

    fun getSnapshot(): Pair<List<ItemStack>, EntityEquipment> {
        val itemsSnapshot = current().playerInventory.map { it.second.copy() }
        val equipmentSnapshot = current().equipmentInventory
        return itemsSnapshot to equipmentSnapshot
    }
}
