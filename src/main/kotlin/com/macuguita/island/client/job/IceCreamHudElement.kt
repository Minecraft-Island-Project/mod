package com.macuguita.island.client.job

import com.macuguita.island.client.ClientEntrypoint
import com.macuguita.island.common.Island
import com.macuguita.island.common.job.JobTicker
import com.macuguita.island.common.job.ice_cream.IceCreamJob
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class IceCreamHudElement : HudElement {

    override fun render(context: GuiGraphics, tickCounter: DeltaTracker) { //TODO: make cute with custom textures
        val font = Minecraft.getInstance().font
        var y = 10

        ClientEntrypoint.iceCreamOrders.forEachIndexed { index, order ->
            var text = "Order ${index + 1}: ${order.toListOfFlavours().joinToString { it.name }}"
            if (order.topping != null) text += ", ${order.topping}"
            context.drawString(font, text, 10, y, 0xFFFFFFFF.toInt())
            y += 10
        }
    }

    companion object {
        val ID = Island.id("ice_cream_job")
    }
}
