/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.block

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.PathComputationType

class DeskBlock(properties: Properties) : HorizontalDirectionalBlock(properties), SimpleWaterloggedBlock {

    companion object {
        val CODEC = simpleCodec(::DeskBlock)
        val WATERLOGGED = BlockStateProperties.WATERLOGGED
    }

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(WATERLOGGED, FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState = this.defaultBlockState()
        .setValue(FACING, context.horizontalDirection.opposite)
        .setValue(WATERLOGGED, context.level.getFluidState(context.clickedPos).type === Fluids.WATER)

    override fun updateShape(
        blockState: BlockState,
        levelReader: LevelReader,
        scheduledTickAccess: ScheduledTickAccess,
        blockPos: BlockPos,
        direction: Direction,
        blockPos2: BlockPos,
        blockState2: BlockState,
        randomSource: RandomSource
    ): BlockState {
        if (blockState.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader))
        }

        return super.updateShape(
            blockState,
            levelReader,
            scheduledTickAccess,
            blockPos,
            direction,
            blockPos2,
            blockState2,
            randomSource
        )
    }

    override fun getFluidState(blockState: BlockState): FluidState =
        if (blockState.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(blockState)

    override fun isPathfindable(blockState: BlockState, pathComputationType: PathComputationType): Boolean = false

    override fun codec(): MapCodec<DeskBlock> = CODEC
}
