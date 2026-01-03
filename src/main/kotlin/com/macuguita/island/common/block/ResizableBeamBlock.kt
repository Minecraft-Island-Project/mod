package com.macuguita.island.common.block

import com.macuguita.island.common.reg.IslandBlockTags
import com.macuguita.island.common.reg.IslandItemTags
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.tags.ItemTags
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.ScheduledTickAccess
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import kotlin.math.max
import kotlin.math.min

class ResizableBeamBlock(properties: Properties, val strippable: Boolean) : Block(properties), SimpleWaterloggedBlock {

    companion object {
        val STRIPPED_BEAM_BLOCKS: MutableMap<Block, Block> = mutableMapOf()

        val FACINGS = Direction.entries.toTypedArray()

        val NORTH = BlockStateProperties.NORTH
        val EAST = BlockStateProperties.EAST
        val SOUTH = BlockStateProperties.SOUTH
        val WEST = BlockStateProperties.WEST
        val UP = BlockStateProperties.UP
        val DOWN = BlockStateProperties.DOWN

        val PROPERTY_BY_DIRECTION = mapOf<Direction, BooleanProperty>(
            Direction.NORTH to NORTH,
            Direction.EAST to EAST,
            Direction.SOUTH to SOUTH,
            Direction.WEST to WEST,
            Direction.UP to UP,
            Direction.DOWN to DOWN
        )

        val RADIUS = IntegerProperty.create("radius", 1, 7)
        val WATERLOGGED = BlockStateProperties.WATERLOGGED

        private fun getDirectionByVec(hit: Vec3, pos: BlockPos, blockState: BlockState): Direction? {
            val radius = blockState.getValue(RADIUS)
            val relativePos = hit.add(-pos.x.toDouble(), -pos.y.toDouble(), -pos.z.toDouble()).scale(16.0)
            if (relativePos.x < (8.0 - radius)) return Direction.WEST
            else if (relativePos.x > (8.0f + radius)) return Direction.EAST
            else if (relativePos.z < (8.0f - radius)) return Direction.NORTH
            else if (relativePos.z > (8.0f + radius)) return Direction.SOUTH
            else if (relativePos.y < (8.0f - radius)) return Direction.DOWN
            else if (relativePos.y > (8.0f + radius)) return Direction.UP
            return null
        }

        private fun shiftRadius(state: BlockState, amount: Int): BlockState {
            val radius: Int = state.getValue(RADIUS)
            var newRadius = radius + amount

            if (newRadius > 7) {
                newRadius -= 7
            } else if (newRadius < 1) {
                newRadius += 7
            }

            return state.setValue(RADIUS, newRadius)
        }

        fun onResizableBeamActivation(
            state: BlockState,
            level: Level,
            pos: BlockPos,
            player: Player,
            hitResult: BlockHitResult
        ) {
            val hand = player.usedItemHand
            val stack = player.getItemInHand(hand)
            val item = stack.item
            if (stack.`is`(ItemTags.AXES)) {
                val stripped = STRIPPED_BEAM_BLOCKS[state.block]
                if (stripped != null) {
                    if (!player.abilities.instabuild) stack.hurtAndBreak(1, player, hand)
                    if (!level.isClientSide) player.awardStat(Stats.ITEM_USED.get(item))

                    var strippedState = stripped.defaultBlockState()
                    for (prop in PROPERTY_BY_DIRECTION.values) {
                        strippedState = strippedState.setValue(prop, state.getValue(prop))
                    }
                    strippedState =
                        strippedState.setValue(WATERLOGGED, state.getValue(WATERLOGGED))
                            .setValue(RADIUS, state.getValue(RADIUS))
                    level.setBlockAndUpdate(pos, strippedState)
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
                    level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0f, 1.0f)
                }
            } else if (stack.`is`(ConventionalItemTags.SHEAR_TOOLS)) {
                if (!player.abilities.instabuild) stack.hurtAndBreak(1, player, hand)
                if (!level.isClientSide) player.awardStat(Stats.ITEM_USED.get(item))
                level.playSound(
                    player,
                    pos.x.toDouble(),
                    pos.y.toDouble(),
                    pos.z.toDouble(),
                    SoundEvents.BEEHIVE_SHEAR,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f
                )

                val newState: BlockState = shiftRadius(state, if (player.isShiftKeyDown) -1 else 1)

                level.setBlockAndUpdate(pos, newState)
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
            } else if (stack.`is`(IslandItemTags.SECATEURS)) {
                val dir: Direction = getDirectionByVec(hitResult.getLocation(), pos, state) ?: hitResult.direction
                val prop: BooleanProperty = PROPERTY_BY_DIRECTION[dir]!!

                val current: Boolean = state.getValue(prop)
                val next = !current

                val newState = state.setValue(prop, next)
                level.setBlockAndUpdate(pos, newState)
                val neighborPos = pos.relative(dir)
                val neighborState = level.getBlockState(neighborPos)

                if (neighborState.block is ResizableBeamBlock && neighborState.`is`(IslandBlockTags.BEAM)) {
                    val opp: BooleanProperty = PROPERTY_BY_DIRECTION[dir.opposite]!!
                    val newNeighborState = neighborState.setValue(opp, next)
                    level.setBlockAndUpdate(neighborPos, newNeighborState)
                }

                level.gameEvent(player, if (next) GameEvent.BLOCK_ATTACH else GameEvent.BLOCK_DETACH, pos)
                if (!player.abilities.instabuild) stack.hurtAndBreak(1, player, hand)
                if (!level.isClientSide) player.awardStat(Stats.ITEM_USED.get(item))
                level.playSound(player, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0f, 1.0f)
            }
        }
    }

    val radiusToFacingsShape = generateRadiusToFacingsShapeMap()

    constructor(properties: Properties) : this(properties, true)

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(RADIUS, 4)
                .setValue(NORTH, false)
                .setValue(EAST, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(WATERLOGGED, false)
        )
    }

    fun isStrippable(): Boolean = this.strippable

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(RADIUS, NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED)
    }

    override fun rotate(blockState: BlockState, rotation: Rotation): BlockState {
        var rotated = blockState
        for (direction in Direction.entries) {
            val newDir = rotation.rotate(direction)
            rotated = rotated.setValue(
                PROPERTY_BY_DIRECTION[newDir]!!,
                blockState.getValue(PROPERTY_BY_DIRECTION[direction]!!)
            )
        }
        return rotated
    }

    override fun mirror(blockState: BlockState, mirror: Mirror): BlockState {
        val rotation = mirror.getRotation(Direction.NORTH)
        if (rotation != Rotation.NONE) {
            return this.rotate(blockState, rotation)
        }

        var mirrored = blockState
        for (direction in Direction.entries) {
            val newDir = mirror.mirror(direction)
            mirrored = mirrored.setValue(
                PROPERTY_BY_DIRECTION[newDir]!!,
                blockState.getValue(PROPERTY_BY_DIRECTION[direction]!!)
            )
        }
        return mirrored
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
        if (blockState.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader))
        }

        return shouldConnectWithNeighbor(
            super.updateShape(
                blockState,
                levelReader,
                scheduledTickAccess,
                blockPos,
                direction,
                blockPos2,
                blockState2,
                randomSource
            ), blockState2, direction
        )
    }

    private fun shouldConnectWithNeighbor(
        blockState: BlockState,
        neighborState: BlockState,
        direction: Direction
    ): BlockState {
        if (!neighborState.`is`(IslandBlockTags.BEAM) || neighborState.block !is ResizableBeamBlock) {
            return blockState
        }

        if (neighborState.getValue(PROPERTY_BY_DIRECTION[direction.opposite]!!)) {
            return blockState.setValue(PROPERTY_BY_DIRECTION[direction]!!, true)
        }
        return blockState
    }

    private fun shouldConnectWithNeighbors(blockState: BlockState, pos: BlockPos, level: Level): BlockState {
        var tempState = blockState
        for (direction in Direction.entries) {
            tempState = shouldConnectWithNeighbor(tempState, level.getBlockState(pos.relative(direction)), direction)
        }
        return tempState
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        val side = blockPlaceContext.clickedFace

        val state = defaultBlockState()
            .setValue(PROPERTY_BY_DIRECTION[side.opposite]!!, true)
            .setValue(PROPERTY_BY_DIRECTION[side]!!, blockPlaceContext.player?.isShiftKeyDown ?: false)
            .setValue(
                WATERLOGGED,
                blockPlaceContext.level.getFluidState(blockPlaceContext.clickedPos).`is`(Fluids.WATER)
            )

        return shouldConnectWithNeighbors(state, blockPlaceContext.clickedPos, blockPlaceContext.level)
    }


    private fun generateRadiusToFacingsShapeMap(): Array<Array<VoxelShape>> {
        return Array(8) { radius ->
            if (radius == 0) emptyArray()
            else generateFacingsToShapeMap(radius / 16.0f)
        }
    }

    private fun generateFacingsToShapeMap(radius: Float): Array<VoxelShape> {
        val f = 0.5f - radius
        val g = 0.5f + radius
        val centerShape = box(
            (f * 16.0f).toDouble(),
            (f * 16.0f).toDouble(),
            (f * 16.0f).toDouble(),
            (g * 16.0f).toDouble(),
            (g * 16.0f).toDouble(),
            (g * 16.0f).toDouble()
        )
        val armShapes = arrayOfNulls<VoxelShape>(FACINGS.size)

        for (i in FACINGS.indices) {
            val direction = FACINGS[i]
            armShapes[i] = Shapes.box(
                0.5 + min(-radius.toDouble(), direction.stepX * 0.5),
                0.5 + min(-radius.toDouble(), direction.stepY * 0.5),
                0.5 + min(-radius.toDouble(), direction.stepZ * 0.5),
                0.5 + max(radius.toDouble(), direction.stepX * 0.5),
                0.5 + max(radius.toDouble(), direction.stepY * 0.5),
                0.5 + max(radius.toDouble(), direction.stepZ * 0.5)
            )
        }

        val facingShapes = Array<VoxelShape>(64) { Shapes.empty() }

        for (mask in 0..63) {
            var shape = centerShape

            for (k in FACINGS.indices) {
                if ((mask and (1 shl k)) != 0) {
                    shape = Shapes.or(shape, armShapes[k]!!)
                }
            }

            facingShapes[mask] = shape
        }

        return facingShapes
    }

    override fun getShape(state: BlockState, world: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val radius: Int = state.getValue(RADIUS)
        val mask: Int = this.getConnectionMask(state)
        return radiusToFacingsShape[radius][mask]
    }

    private fun getConnectionMask(state: BlockState): Int {
        var mask = 0

        /*
		 * representation of the int mask where 1 would be connected
		 * 0 0 0 0 0 0
		 * ^ ^ ^ ^ ^ ^
		 * N E S W U D
		 */
        for (i in FACINGS.indices) {
            if (state.getValue(PROPERTY_BY_DIRECTION[FACINGS[i]]!!)) {
                mask = mask or (1 shl i)
            }
        }

        return mask
    }

    public override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

}
