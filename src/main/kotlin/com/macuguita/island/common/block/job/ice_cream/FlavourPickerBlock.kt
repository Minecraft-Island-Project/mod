/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.block.job.ice_cream

import com.macuguita.island.common.data_components.IceCreamComponent
import com.macuguita.island.common.job.ice_cream.IceCreamFlavour
import com.macuguita.island.common.reg.IslandDataComponents
import com.macuguita.island.common.reg.IslandItemTags
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.BlockHitResult

class FlavourPickerBlock(properties: Properties) : HorizontalDirectionalBlock(properties) {

    companion object {
        val FLAVOUR = EnumProperty.create("ice_cream_flavour", IceCreamFlavour::class.java)

        val CODEC = simpleCodec(::FlavourPickerBlock)
    }

    init {
        registerDefaultState(
            defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(FLAVOUR, IceCreamFlavour.VANILLA)
        )
    }

    override fun codec(): MapCodec<FlavourPickerBlock> = CODEC

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(FACING, FLAVOUR)
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(FACING, blockPlaceContext.horizontalDirection.opposite)

    override fun useItemOn(
        itemStack: ItemStack,
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (!itemStack.`is`(IslandItemTags.ICE_CREAM_HOLDER)) return InteractionResult.PASS
        if (level.isClientSide) return InteractionResult.SUCCESS

        val blockFlavour = blockState.getValue(FLAVOUR)
        val flavourKey = IslandDataComponents.ICE_CREAM_COMPONENT.get()

        @Suppress("UNREACHABLE_CODE")
        itemStack.get(flavourKey)?.let { component ->
            if (component.asSequenceOfFlavours()
                    .contains(blockFlavour) || component.isFull()
            ) return InteractionResult.PASS

            val newFlavoursComponent = IceCreamComponent.fromList(component.toListOfFlavours() + blockFlavour)
            itemStack.set(flavourKey, newFlavoursComponent)
            return InteractionResult.SUCCESS
        } ?: run {
            itemStack.set(flavourKey, IceCreamComponent(blockFlavour, null, null, null))
            return InteractionResult.SUCCESS
        }
    }
}
