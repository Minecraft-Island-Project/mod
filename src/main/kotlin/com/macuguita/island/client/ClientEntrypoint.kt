/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.client

import com.macuguita.island.client.job.IceCreamHudElement
import com.macuguita.island.client.job.JobZoneMasterBlockEntityRenderer
import com.macuguita.island.common.data_components.IceCreamComponent
import com.macuguita.island.common.network.s2c.IceCreamSyncOrdersS2CPacket
import com.macuguita.island.common.reg.IslandBlockEntities
import com.macuguita.island.common.reg.IslandObjects
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraft.client.renderer.chunk.ChunkSectionLayer


object ClientEntrypoint : ClientModInitializer {

    var iceCreamOrders: List<IceCreamComponent> = emptyList()
        get() = field.toList()

    override fun onInitializeClient() {
        BlockRenderLayerMap.putBlocks(
            ChunkSectionLayer.CUTOUT,
            IslandObjects.SMALL_LOG_OAK_TABLE.get(),
        )
        HudElementRegistry.addLast(
            IceCreamHudElement.ID,
            IceCreamHudElement()
        )
        BlockEntityRenderers.register(
            IslandBlockEntities.JOB_ZONE_MASTER_BLOCK_ENTITY.get(),
            ::JobZoneMasterBlockEntityRenderer
        )
        ClientPlayNetworking.registerGlobalReceiver(
            IceCreamSyncOrdersS2CPacket.ID,
            { payload, context ->
                iceCreamOrders = payload.orders
            })
    }
}
