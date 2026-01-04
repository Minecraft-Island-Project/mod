/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.block.jobs

import com.macuguita.island.common.job.JobTicker
import com.macuguita.island.common.reg.IslandItemTags
import com.macuguita.island.common.reg.IslandJobs
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.EntityCollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class JobZoneBlock(properties: Properties, private val jobId: Identifier) : Block(properties) {

    override fun tick(
        blockState: BlockState,
        serverLevel: ServerLevel,
        blockPos: BlockPos,
        randomSource: RandomSource
    ) {
        val playersInZone = serverLevel.getPlayers { p -> p.boundingBox.intersects(AABB(blockPos)) }

        for (player in playersInZone) {
            if (JobTicker.getJob(player) == null) {
                IslandJobs.JOBS[jobId]?.let { JobTicker.startJob(player, it) }
            }
        }

        JobTicker.activeJobs.forEach { (uuid, _) ->
            val player = serverLevel.getPlayerByUUID(uuid)
            if (player != null && player is ServerPlayer && !playersInZone.contains(player)) {
                JobTicker.endJob(player)
            }
        }
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        if (collisionContext is EntityCollisionContext && collisionContext.entity is Player && (collisionContext.entity as Player).isHolding {
                it.`is`(IslandItemTags.JOB_AREA)
            }) {
            return Shapes.block()
        }
        return Shapes.empty()
    }

    override fun getCollisionShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return Shapes.empty()
    }

    override fun skipRendering(blockState: BlockState, blockState2: BlockState, direction: Direction): Boolean {
        return true
    }

}
