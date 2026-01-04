/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.item

import com.macuguita.island.common.reg.IslandDataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import java.util.function.Consumer

class IceCreamConeItem(properties: Properties) : Item(properties) {

    override fun appendHoverText(
        itemStack: ItemStack,
        tooltipContext: TooltipContext,
        tooltipDisplay: TooltipDisplay,
        consumer: Consumer<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag)
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
