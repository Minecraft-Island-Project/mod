package com.macuguita.island.common.block.job.ice_cream;

import com.macuguita.island.common.job.JobTicker
import com.macuguita.island.common.job.ice_cream.IceCreamJob
import com.macuguita.island.common.reg.IslandDataComponents
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class SubmitIceCreamBlock(properties: Properties) : HorizontalDirectionalBlock(properties) {

    companion object {
        val CODEC = simpleCodec(::SubmitIceCreamBlock)
    }

    override fun codec(): MapCodec<SubmitIceCreamBlock> = CODEC

    override fun useItemOn(
        itemStack: ItemStack,
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS

        val serverPlayer = player as? ServerPlayer ?: return InteractionResult.PASS

        if (!itemStack.has(IslandDataComponents.ICE_CREAM_COMPONENT.get()))
            return InteractionResult.PASS

        val job = JobTicker.getJob(serverPlayer) as? IceCreamJob
            ?: return InteractionResult.PASS

        val accepted = job.submitIceCream(serverPlayer, itemStack)

        if (!accepted) {
            return InteractionResult.PASS
        }

        itemStack.shrink(1)

        return InteractionResult.CONSUME
    }
}
