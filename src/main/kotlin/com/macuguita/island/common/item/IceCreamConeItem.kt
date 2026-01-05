/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.item

import com.macuguita.island.common.reg.IslandDataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.Level
import java.util.function.Consumer

class IceCreamConeItem(properties: Properties) : Item(properties.stacksTo(1)) {

    companion object {
        fun onIceCreamConeActivation(
            player: Player, level: Level, hand: InteractionHand
        ): InteractionResult {
            if (level.isClientSide) return InteractionResult.SUCCESS

            if (player.isShiftKeyDown) {
                val stack = player.getItemInHand(hand)

                if (stack.has(IslandDataComponents.ICE_CREAM_COMPONENT.get())) {
                    stack.remove(IslandDataComponents.ICE_CREAM_COMPONENT.get())

                    player.setItemInHand(hand, stack)
                    player.inventory.setChanged()
                }

                return InteractionResult.SUCCESS
            }

            return InteractionResult.PASS
        }
    }

    override fun appendHoverText(
        itemStack: ItemStack,
        tooltipContext: TooltipContext,
        tooltipDisplay: TooltipDisplay,
        consumer: Consumer<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag)
        consumer.accept(Component.literal("tooltip.ice_cream_holder_item"))
        itemStack.get(IslandDataComponents.ICE_CREAM_COMPONENT.get())?.let { component ->
            consumer.accept(Component.literal(component.firstFlavour.serializedName))
            if (component.secondFlavour != null)
                consumer.accept(Component.literal(component.secondFlavour.serializedName))
            if (component.thirdFlavour != null)
                consumer.accept(Component.literal(component.thirdFlavour.serializedName))
            if (component.topping != null)
                consumer.accept(Component.literal(component.topping.serializedName))
        }
    }
}
