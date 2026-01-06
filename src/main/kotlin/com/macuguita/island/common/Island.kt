/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common

import com.google.common.reflect.Reflection
import com.macuguita.island.client.job.gui.JobZoneMasterScreen
import com.macuguita.island.common.attachments.JoinedServer
import com.macuguita.island.common.attachments.SavedInventory
import com.macuguita.island.common.block.ResizableBeamBlock
import com.macuguita.island.common.block.entity.JobZoneMasterBlockEntity
import com.macuguita.island.common.commands.CommandRegistrator
import com.macuguita.island.common.item.IceCreamConeItem
import com.macuguita.island.common.job.JobTicker
import com.macuguita.island.common.network.cs2.JobZoneUpdateC2SPacket
import com.macuguita.island.common.network.s2c.IceCreamSyncOrdersS2CPacket
import com.macuguita.island.common.reg.*
import folk.sisby.kaleido.api.WrappedConfig
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import net.minecraft.tags.ItemTags
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


object Island : ModInitializer {

    const val MOD_ID: String = "island"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    val CONFIG: Config =
        WrappedConfig.createToml(FabricLoader.getInstance().configDir, "", MOD_ID, Config::class.java)

    fun id(name: String): Identifier {
        return Identifier.fromNamespaceAndPath(MOD_ID, name)
    }

    fun <T> Optional<T>.orNull(): T? = orElse(null)

    fun Player.openJobZoneMasterScreen(blockEntity: JobZoneMasterBlockEntity) {
        if (this.level().isClientSide) {
            val minecraft = Minecraft.getInstance()
            val screen = minecraft.screen
            minecraft.setScreen(JobZoneMasterScreen(blockEntity, screen))
        }
    }

    override fun onInitialize() {
        initRegistries()

        JobTicker.init()

        registerPayloads()

        ServerPlayNetworking.registerGlobalReceiver(
            JobZoneUpdateC2SPacket.ID,
            JobZoneUpdateC2SPacket.Receiver()
        )

        UseItemCallback.EVENT.register { player, level, hand ->
            val item = player.getItemInHand(hand)
            if (item.item is IceCreamConeItem && item.`is`(IslandItemTags.ICE_CREAM_HOLDER)) {
                IceCreamConeItem.onIceCreamConeActivation(player, level, hand)
            }

            InteractionResult.PASS
        }

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

    fun initRegistries() {
        Reflection.initialize(JoinedServer::class.java)
        Reflection.initialize(SavedInventory::class.java)
        IslandObjects.init()
        IslandBlockEntities.init()
        IslandCreativeModeTabs.init()
        IslandDataComponents.init()
    }

    fun registerPayloads() {
        PayloadTypeRegistry.playC2S().register(JobZoneUpdateC2SPacket.ID, JobZoneUpdateC2SPacket.CODEC)
        PayloadTypeRegistry.playS2C().register(IceCreamSyncOrdersS2CPacket.ID, IceCreamSyncOrdersS2CPacket.CODEC)
    }

    fun registerEvents() {

    }
}
