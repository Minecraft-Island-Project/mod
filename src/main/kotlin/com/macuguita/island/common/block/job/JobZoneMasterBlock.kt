/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.block.job


import com.macuguita.island.common.block.entity.JobZoneMasterBlockEntity
import com.macuguita.island.common.reg.IslandBlockEntities
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class JobZoneMasterBlock(
    properties: Properties,
) : BaseEntityBlock(properties) {

    companion object {
        val CODEC: MapCodec<JobZoneMasterBlock> = simpleCodec(::JobZoneMasterBlock)
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = CODEC

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return JobZoneMasterBlockEntity(pos, state)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is JobZoneMasterBlockEntity) {
            return if (blockEntity.usedBy(player)) InteractionResult.SUCCESS else InteractionResult.PASS
        }
        return InteractionResult.PASS
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (level.isClientSide) null else createTickerHelper(
            blockEntityType,
            IslandBlockEntities.JOB_ZONE_MASTER_BLOCK_ENTITY.get(),
            JobZoneMasterBlockEntity::serverTick)
    }

}
