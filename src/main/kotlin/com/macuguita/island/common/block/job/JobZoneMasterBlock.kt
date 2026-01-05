/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.block.job


import com.macuguita.island.common.Island
import com.macuguita.island.common.block.entity.JobZoneMasterBlockEntity
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
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

    override fun tick(
        state: BlockState,
        level: ServerLevel,
        pos: BlockPos,
        random: RandomSource
    ) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is JobZoneMasterBlockEntity) {
            blockEntity.tick(level)
        }
        level.scheduleTick(pos, this, 1)
    }

    override fun onPlace(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        oldState: BlockState,
        moved: Boolean
    ) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1)
        }
        super.onPlace(state, level, pos, oldState, moved)
    }
}
