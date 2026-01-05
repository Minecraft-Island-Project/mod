/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common

import com.macuguita.island.client.job.gui.JobZoneMasterScreen
import com.macuguita.island.common.block.ResizableBeamBlock
import com.macuguita.island.common.block.entity.JobZoneMasterBlockEntity
import com.macuguita.island.common.commands.CommandRegistrator
import com.macuguita.island.common.job.JobTicker
import com.macuguita.island.common.network.JobZoneUpdateC2SPacket
import com.macuguita.island.common.reg.*
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import net.minecraft.tags.ItemTags
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Block
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


object Island : ModInitializer {

    const val MOD_ID: String = "island"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    fun id(name: String): Identifier {
        return Identifier.fromNamespaceAndPath(MOD_ID, name)
    }

    fun <T> Optional<T>.orNull(): T? = orElse(null)

    fun Player.openJobZoneMasterScreen(blockEntity: JobZoneMasterBlockEntity) {
        if (this.level().isClientSide) {
            Minecraft.getInstance().setScreen(JobZoneMasterScreen(blockEntity))
        }
    }

    override fun onInitialize() {
        IslandObjects.init()
        IslandBlockEntities.init()
        IslandCreativeModeTabs.init()
        IslandDataComponents.init()
        JobTicker.init()

        PayloadTypeRegistry.playC2S().register(JobZoneUpdateC2SPacket.ID, JobZoneUpdateC2SPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(
            JobZoneUpdateC2SPacket.ID,
            { payload, context ->
                val server = context.server()

                server.execute {
                    val level = context.player().level()
                    val blockEntityPos = payload.blockPos
                    (level.getBlockEntity(blockEntityPos) as? JobZoneMasterBlockEntity)?.let { blockEntity ->
                            blockEntity.jobId = payload.jobId
                            blockEntity.zonePos = payload.zonePos
                            blockEntity.zoneSize = payload.zoneSize
                            blockEntity.showBoundingBox = payload.showBoundingBox

                            blockEntity.setChanged()
                            level.sendBlockUpdated(
                                blockEntityPos,
                                blockEntity.blockState,
                                blockEntity.blockState,
                                Block.UPDATE_ALL
                            )
                        }
                }
            }
        )

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
