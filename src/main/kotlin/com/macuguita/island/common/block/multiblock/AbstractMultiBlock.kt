/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.block.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.material.PushReaction

abstract class AbstractMultiBlock(
    properties: Properties
) : Block(
    properties.pushReaction(PushReaction.BLOCK)
) {

    companion object {
        private val DEFAULT_SHAPE: Array<Array<IntArray>> = arrayOf(arrayOf(intArrayOf(1)))

        val MULTIBLOCK_PART = IntegerProperty.create("part", 1, 2)
        val FACING = BlockStateProperties.FACING
    }

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(getMultiblockPart(), 1)
                .setValue(FACING, Direction.NORTH)
        )
    }

    protected open fun getMultiblockShape(): Array<Array<IntArray>> {
        return DEFAULT_SHAPE
    }

    protected open fun getMultiblockPart(): IntegerProperty {
        return MULTIBLOCK_PART
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(FACING, getMultiblockPart())
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState? {
        if (!canPlaceMultiblock(blockPlaceContext)) {
            return null
        }
        return defaultBlockState().setValue(FACING, blockPlaceContext.horizontalDirection)
    }

    private fun canPlaceMultiblock(blockPlaceContext: BlockPlaceContext): Boolean {
        val level = blockPlaceContext.level
        val origin = blockPlaceContext.clickedPos
        val facing = blockPlaceContext.horizontalDirection
        val shape = getMultiblockShape()

        if (level.isOutsideBuildHeight(origin.y + shape.size)) {
            return false
        }

        for (y in shape.indices) {
            for (x in shape[y].indices) {
                for (z in shape[y][x].indices) {
                    if (shape[y][x][z] != 0) {
                        val pos = getOffsetPosition(origin, facing, x, y, z)
                        if (!level.getBlockState(pos).canBeReplaced()) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        oldBlockState: BlockState,
        bl: Boolean
    ) {
        if (level.isClientSide || oldBlockState.`is`(this)) {
            return
        }
        if (blockState.getValue(getMultiblockPart()) != 1) {
            return
        }

        val facing = blockState.getValue(FACING)
        val shape = getMultiblockShape()

        for (y in shape.indices) {
            for (x in shape[y].indices) {
                for (z in shape[y][x].indices) {
                    if ((x == 0 && y == 0 && z == 0) || shape[y][x][z] == 0) {
                        continue
                    }

                    val targetPos = getOffsetPosition(blockPos, facing, x, y, z)
                    val targetState = defaultBlockState()
                        .setValue(getMultiblockPart(), shape[y][x][z])
                        .setValue(FACING, facing)

                    level.setBlock(targetPos, targetState, UPDATE_ALL)
                }
            }
        }
        super.onPlace(blockState, level, blockPos, oldBlockState, bl)
    }

    override fun canSurvive(blockState: BlockState, levelReader: LevelReader, blockPos: BlockPos): Boolean {
        if (!levelReader.getBlockState(blockPos).`is`(this)) {
            return true
        }
        if (blockState.getValue(getMultiblockPart()) != 1) {
            return true
        }
        return isMultiblockIntact(blockState, levelReader, blockPos)
    }

    private fun isMultiblockIntact(blockState: BlockState, levelReader: LevelReader, blockPos: BlockPos): Boolean {
        val facing = blockState.getValue(FACING)
        val shape = getMultiblockShape()

        for (y in shape.indices) {
            for (x in shape[y].indices) {
                for (z in shape[y][x].indices) {
                    val expectedPart = shape[y][x][z]
                    if (expectedPart == 0) {
                        continue
                    }

                    val checkPos = getOffsetPosition(blockPos, facing, x, y, z)
                    val checkState = levelReader.getBlockState(checkPos)

                    if (!checkState.hasProperty(getMultiblockPart()) ||
                        checkState.getValue(getMultiblockPart()) != expectedPart
                    ) {
                        return false
                    }
                }
            }
        }
        return true
    }

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
        val primaryPos = findPrimaryBlock(blockPos, levelReader)

        if (primaryPos != null) {
            if (!canSurvive(levelReader.getBlockState(primaryPos), levelReader, primaryPos)) {
                scheduledTickAccess.scheduleTick(blockPos, this, 1)
            }
        } else {
            scheduledTickAccess.scheduleTick(blockPos, this, 1)
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

    override fun tick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource
    ) {
        val primaryPos = findPrimaryBlock(blockPos, serverLevel)

        if (primaryPos != null) {
            if (!canSurvive(serverLevel.getBlockState(primaryPos), serverLevel, primaryPos)) {
                serverLevel.destroyBlock(blockPos, true)
            }
        } else {
            serverLevel.destroyBlock(blockPos, true)
        }

        super.tick(blockState, serverLevel, blockPos, randomSource)
    }

    private fun findPrimaryBlock(currentPos: BlockPos, level: LevelReader): BlockPos? {
        val currentState = level.getBlockState(currentPos)

        if (!currentState.hasProperty(FACING)) {
            return null
        }

        if (currentState.getValue(getMultiblockPart()) == 1) {
            return currentPos
        }

        val facing = currentState.getValue(FACING)
        val currentPart = currentState.getValue(getMultiblockPart())
        val shape = getMultiblockShape()

        for (y in shape.indices) {
            for (x in shape[y].indices) {
                for (z in shape[y][x].indices) {
                    if (shape[y][x][z] == currentPart) {
                        val xOffset = -getXOffset(facing, x, z)
                        val zOffset = -getZOffset(facing, x, z)
                        val primaryPos = currentPos.offset(xOffset, -y, zOffset)

                        val primaryState = level.getBlockState(primaryPos)
                        if (primaryState.`is`(this) && primaryState.getValue(FACING) == facing) {
                            return primaryPos
                        }
                    }
                }
            }
        }
        return null
    }

    /**
     * Gets the world position for a multiblock part at the given shape coordinates
     */
    private fun getOffsetPosition(origin: BlockPos, facing: Direction, x: Int, y: Int, z: Int): BlockPos {
        val xOffset = getXOffset(facing, x, z)
        val zOffset = getZOffset(facing, x, z)
        return origin.offset(xOffset, y, zOffset)
    }

    /**
     * Calculates X offset based on facing direction and shape coordinates
     */
    private fun getXOffset(facing: Direction, x: Int, z: Int): Int {
        return when (facing) {
            Direction.NORTH -> x
            Direction.SOUTH -> -x
            Direction.EAST -> z
            Direction.WEST -> -z
            else -> 0
        }
    }

    /**
     * Calculates Z offset based on facing direction and shape coordinates
     */
    private fun getZOffset(facing: Direction, x: Int, z: Int): Int {
        return when (facing) {
            Direction.NORTH -> -z
            Direction.SOUTH -> z
            Direction.EAST -> x
            Direction.WEST -> -x
            else -> 0
        }
    }
}
