/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.job.ice_cream

import com.macuguita.island.common.Island
import com.macuguita.island.common.api.Job
import com.macuguita.island.common.attachments.SavedInventory
import com.macuguita.island.common.data_components.IceCreamComponent
import com.macuguita.island.common.job.JobTicker
import com.macuguita.island.common.network.s2c.IceCreamSyncOrdersS2CPacket
import com.macuguita.island.common.reg.IslandDataComponents
import com.macuguita.island.common.reg.IslandItemTags
import com.macuguita.island.common.reg.IslandObjects
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import java.util.*

class IceCreamJob(id: Identifier) : Job(id) {

    val orders: MutableList<IceCreamComponent> = mutableListOf()
    private var ordersDirty = false

    private val random = Random()
    private var ticksSinceLastOrder = 0
    private val minTicksBetweenOrders = Island.CONFIG.common.ticksForNextIceCream

    private fun addOrder(order: IceCreamComponent) {
        orders.add(order)
        ordersDirty = true
    }

    private fun removeOrder(order: IceCreamComponent) {
        orders.remove(order)
        ordersDirty = true
    }

    private fun syncOrders(player: ServerPlayer, list: List<IceCreamComponent> = orders.toList()) =
        ServerPlayNetworking.send(player, IceCreamSyncOrdersS2CPacket(list))

    private fun syncOrdersToAllPlayers(serverLevel: ServerLevel) {
        JobTicker.forEachActiveJob(serverLevel.server) { uuid, jobId ->
            if (jobId == id) {
                serverLevel.getPlayerByUUID(uuid)?.let { player ->
                    syncOrders(player as ServerPlayer)
                }
            }
        }
    }

    fun tickCommon() {
    }

    override fun tickClient() {
        tickCommon()
    }

    override fun tickServer(serverLevel: ServerLevel) {
        tickCommon()
        ticksSinceLastOrder++

        if (orders.size < 10 && ticksSinceLastOrder >= minTicksBetweenOrders && random.nextInt(4) == 1) {
            addOrder(generateRandomOrder())
            ticksSinceLastOrder = 0
        }

        if (ordersDirty) {
            syncOrdersToAllPlayers(serverLevel)
            ordersDirty = false
        }
    }

    override fun startJob(player: ServerPlayer, hadActiveJob: Boolean) {
        val savedInventoryComponent = SavedInventory[player]
        if (!hadActiveJob) {
            savedInventoryComponent.saveInventory(player.inventory)
        }

        player.inventory.clearContent()

        val coneStack = ItemStack(IslandObjects.ICE_CREAM_CONE.get())
        player.inventory.setItem(0, coneStack)

        val cupStack = ItemStack(IslandObjects.ICE_CREAM_CUP.get())
        player.inventory.setItem(1, cupStack)

        player.inventory.setChanged()

        syncOrders(player)
    }

    override fun endJob(player: ServerPlayer) {
        val savedInventoryComponent = SavedInventory[player]

        if (savedInventoryComponent.hasSavedInventory()) {
            player.inventory.clearContent()
            savedInventoryComponent.loadInventory(player.inventory)
            player.inventory.setChanged()
        }

        syncOrders(player, emptyList())
    }

    fun submitIceCream(player: ServerPlayer, itemStack: ItemStack): Boolean {
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

        removeOrder(order)

        return true
    }

    private fun generateRandomOrder(): IceCreamComponent {
        val flavourCount = 1 + random.nextInt(3)
        val flavours = IceCreamFlavour.entries.shuffled().take(flavourCount)
        val topping = if (random.nextBoolean()) IceCreamTopping.entries.random() else null
        return IceCreamComponent.fromList(flavours, topping)
    }
}
