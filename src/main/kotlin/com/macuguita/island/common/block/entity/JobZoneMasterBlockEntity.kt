/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.block.entity

import com.macuguita.island.common.Island.openJobZoneMasterScreen
import com.macuguita.island.common.job.JobTicker
import com.macuguita.island.common.reg.IslandBlockEntities
import com.macuguita.island.common.reg.IslandJobs
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.GameMasterBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB

class JobZoneMasterBlockEntity(
    pos: BlockPos,
    state: net.minecraft.world.level.block.state.BlockState,
) : BlockEntity(IslandBlockEntities.JOB_ZONE_MASTER_BLOCK_ENTITY.get(), pos, state),
    GameMasterBlock {

    companion object {
        private const val MAX_SIZE = 48

        @JvmStatic
        fun serverTick(level: Level, blockPos: BlockPos, blockState: BlockState, jobZoneMasterBlockEntity: JobZoneMasterBlockEntity) {
            if (level !is ServerLevel)
                return
            val jobId = jobZoneMasterBlockEntity.jobId
            val zoneAABB = calculateZoneAABB(blockPos, jobZoneMasterBlockEntity.zonePos, jobZoneMasterBlockEntity.zoneSize)
            val playersInZone = level.getPlayers { p ->
                p.boundingBox.intersects(zoneAABB)
            }

            IslandJobs.JOBS[jobId] ?: return

            for (player in playersInZone) {
                val activeJob = JobTicker.getJob(player)
                if (activeJob?.id != jobId) {
                    JobTicker.startJob(player, jobId)
                }
            }

            JobTicker.forEachActiveJob { uuid, activeJobId ->
                if (activeJobId == jobId) {
                    val player = level.server.playerList.players.first { it.uuid == uuid }
                    val isOutsideZone = !player.boundingBox.intersects(zoneAABB.inflate(0.5))
                    if (isOutsideZone) {
                        JobTicker.requestEndJob(player)
                    }
                }
            }
        }

        @JvmStatic
        private fun calculateZoneAABB(blockPos: BlockPos, zonePos: BlockPos, zoneSize: Vec3i): AABB {
            val startPos = blockPos.offset(zonePos)
            return AABB(
                startPos.x.toDouble(),
                startPos.y.toDouble(),
                startPos.z.toDouble(),
                (startPos.x + zoneSize.x).toDouble(),
                (startPos.y + zoneSize.y).toDouble(),
                (startPos.z + zoneSize.z).toDouble()
            )
        }
    }

    var jobId: Identifier = IslandJobs.ICE_CREAM_ID
        set(newJobId) {
            field = newJobId
            setChanged()
            if (level != null && !level!!.isClientSide) {
                level!!.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
            }
        }

    var zonePos: BlockPos = BlockPos(0, 0, 0)
        set(pos) {
            field = pos
            setChanged()
            if (level != null && !level!!.isClientSide) {
                level!!.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
            }
        }

    var zoneSize: Vec3i = Vec3i(10, 5, 10)
        set(size) {
            field = Vec3i(
                Mth.clamp(size.x, 1, MAX_SIZE),
                Mth.clamp(size.y, 1, MAX_SIZE),
                Mth.clamp(size.z, 1, MAX_SIZE)
            )
            setChanged()
            if (level != null && !level!!.isClientSide) {
                level!!.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL)
            }
        }

    var showBoundingBox: Boolean = true
        set(value) {
            field = value
            setChanged()
        }

    override fun saveAdditional(valueOutput: ValueOutput) {
        super.saveAdditional(valueOutput)
        valueOutput.putString("JobId", jobId.toString())
        valueOutput.putInt("posX", zonePos.x)
        valueOutput.putInt("posY", zonePos.y)
        valueOutput.putInt("posZ", zonePos.z)
        valueOutput.putInt("sizeX", zoneSize.x)
        valueOutput.putInt("sizeY", zoneSize.y)
        valueOutput.putInt("sizeZ", zoneSize.z)
        valueOutput.putBoolean("showBoundingBox", showBoundingBox)
    }

    override fun loadAdditional(valueInput: ValueInput) {
        super.loadAdditional(valueInput)
        val x = Mth.clamp(valueInput.getInt("posX").get(), -MAX_SIZE, MAX_SIZE)
        val y = Mth.clamp(valueInput.getInt("posY").get(), -MAX_SIZE, MAX_SIZE)
        val z = Mth.clamp(valueInput.getInt("posZ").get(), -MAX_SIZE, MAX_SIZE)
        zonePos = BlockPos(x, y, z)

        val sizeX = Mth.clamp(valueInput.getInt("sizeX").get(), 1, MAX_SIZE)
        val sizeY = Mth.clamp(valueInput.getInt("sizeY").get(), 1, MAX_SIZE)
        val sizeZ = Mth.clamp(valueInput.getInt("sizeZ").get(), 1, MAX_SIZE)
        zoneSize = Vec3i(sizeX, sizeY, sizeZ)

        showBoundingBox = valueInput.getBooleanOr("showBoundingBox", false)
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        return saveWithoutMetadata(registries)
    }

    fun usedBy(player: Player): Boolean {
        if (!player.canUseGameMasterBlocks()) {
            return false
        }

        if (player.level().isClientSide) {
            player.openJobZoneMasterScreen(this)
        }

        return true
    }
}
