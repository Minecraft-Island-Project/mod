/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.job.ice_cream

import com.macuguita.island.common.api.Job
import com.macuguita.island.common.attachments.SavedInventory
import com.macuguita.island.common.attachments.SavedInventoryAttachedData
import com.macuguita.island.common.data_components.IceCreamComponent
import com.macuguita.island.common.job.JobTicker
import com.macuguita.island.common.reg.IslandDataComponents
import com.macuguita.island.common.reg.IslandItemTags
import com.macuguita.island.common.reg.IslandObjects
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import java.util.*

class IceCreamJob(id: Identifier) : Job(id) {

    val orders: MutableList<IceCreamComponent> = Collections.synchronizedList(mutableListOf())

    private val random = Random()

    fun tickCommon() {
    }

    override fun tickClient() {
        tickCommon()
    }

    override fun tickServer(serverLevel: ServerLevel) {
        tickCommon()
        if (orders.count() >= 10) return
        if (random.nextInt(200) == 0) {
            orders.add(generateRandomOrder())
        }
    }

    override fun startJob(player: ServerPlayer) {
        val savedInventoryComponent = SavedInventory[player]
        if (savedInventoryComponent.isWorking) return
        savedInventoryComponent.saveInventory(player.inventory)
        player.inventory.clearContent()

        val coneStack = ItemStack(IslandObjects.ICE_CREAM_CONE.get())
        player.inventory.setItem(0, coneStack)

        val cupStack = ItemStack(IslandObjects.ICE_CREAM_CUP.get())
        player.inventory.setItem(1, cupStack)

        player.inventory.setChanged()
    }

    override fun endJob(player: ServerPlayer) {
        player.inventory.clearContent()
        SavedInventory[player].loadInventory(player.inventory)
        player.inventory.setChanged()
    }

    fun submitIceCream(player: Player, itemStack: ItemStack): Boolean {

        val iceCream = itemStack.get(IslandDataComponents.ICE_CREAM_COMPONENT.get())!!
        val item = itemStack.item
        if (!itemStack.`is`(IslandItemTags.ICE_CREAM_HOLDER))
            return false
        val order = orders.firstOrNull {
            it.toListOfFlavours().toSet() == iceCream.toListOfFlavours().toSet() &&
                    it.topping == iceCream.topping
        } ?: return false

        if (itemStack.`is`(IslandObjects.ICE_CREAM_CONE.get()))
            player.inventory.setItem(0, ItemStack(item))
        if (itemStack.`is`(IslandObjects.ICE_CREAM_CUP.get()))
            player.inventory.setItem(1, ItemStack(item))
        player.inventory.setChanged()

        orders.remove(order)

        return true
    }

    private fun generateRandomOrder(): IceCreamComponent {
        val flavourCount = 1 + random.nextInt(3)
        val flavours = IceCreamFlavour.entries.shuffled().take(flavourCount)
        val topping = if (random.nextBoolean()) IceCreamTopping.entries.random() else null
        return IceCreamComponent.fromList(flavours, topping)
    }
}


