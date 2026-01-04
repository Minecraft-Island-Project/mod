/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.job.ice_cream

import com.macuguita.island.common.Island
import com.macuguita.island.common.job.JobTicker
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics

class IceCreamHudElement : HudElement {
    override fun render(context: GuiGraphics, tickCounter: DeltaTracker) {
        val player = Minecraft.getInstance().player ?: return
        val job = JobTicker.getJob(player) as? IceCreamJob ?: return

        val font = Minecraft.getInstance().font
        var y = 10

        job.orders.forEachIndexed { index, order ->
            val text = "Order ${index + 1}: ${order.toListOfFlavours().joinToString { it.name }}"
            context.drawString(font, text, 10, y, 0xFFFFFF)
            y += 10
        }
    }

    companion object {
        val ID = Island.id("ice_cream_job")
    }
}
