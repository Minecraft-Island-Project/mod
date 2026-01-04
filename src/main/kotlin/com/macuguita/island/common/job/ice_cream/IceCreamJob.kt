/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.job.ice_cream

import com.macuguita.island.common.api.Job
import com.macuguita.island.common.data_components.IceCreamComponent
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import java.util.*

class IceCreamJob(id: Identifier) : Job(id) {

    val orders: MutableList<IceCreamComponent> = mutableListOf()

    private val random = Random()

    override fun tickCommon() {
        if (orders.count() > 10) return
        // Generate new orders occasionally
        if (Random().nextInt(200) == 0) {
            orders.add(generateRandomOrder())
        }
    }

    override fun tickClient() {
    }

    override fun tickServer(serverLevel: ServerLevel) {
    }

    override fun startJob() {
    }

    override fun endJob() {
    }

    fun submitIceCream(playerUUID: UUID, iceCream: IceCreamComponent): Boolean {
        // Find the first order that matches the submitted ice cream
        val order = orders.firstOrNull { it == iceCream } ?: return false

        // Remove it from the global orders list
        orders.remove(order)

        // Could give rewards here, e.g.:
        // givePlayerCoins(playerUUID, 5)

        return true
    }

    private fun generateRandomOrder(): IceCreamComponent {
        val flavours = IceCreamFlavour.entries.shuffled().take(1 + random.nextInt(2))
        val topping = if (random.nextBoolean()) IceCreamTopping.entries.random() else null
        return IceCreamComponent.fromList(flavours, topping)
    }
}


