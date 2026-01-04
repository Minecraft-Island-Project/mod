/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common

import com.macuguita.island.common.block.ResizableBeamBlock
import com.macuguita.island.common.commands.CommandRegistrator
import com.macuguita.island.common.reg.IslandCreativeModeTabs
import com.macuguita.island.common.reg.IslandItemTags
import com.macuguita.island.common.reg.IslandObjects
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.resources.Identifier
import net.minecraft.tags.ItemTags
import net.minecraft.world.InteractionResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Island : ModInitializer {

    const val MOD_ID: String = "island"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    fun id(name: String): Identifier {
        return Identifier.fromNamespaceAndPath(MOD_ID, name)
    }

    override fun onInitialize() {
        IslandObjects.init()
        IslandCreativeModeTabs.init()

        UseBlockCallback.EVENT.register { player, level, hand, hitResult ->
            val pos = hitResult.blockPos
            val item = player.getItemInHand(hand)
            val state = level.getBlockState(pos)
            val block = state.block

            if ((item.`is`(ItemTags.AXES)
                        || item.`is`(ConventionalItemTags.SHEAR_TOOLS)
                        || item.`is`(IslandItemTags.SECATEURS))
                && block is ResizableBeamBlock
                && block.isStrippable()
            ) {
                ResizableBeamBlock.onResizableBeamActivation(state, level, pos, player, hitResult)
                return@register InteractionResult.SUCCESS
            }

            InteractionResult.PASS
        }
        CommandRegistrationCallback.EVENT.register(CommandRegistrator.RegisterCommands())
    }
}
