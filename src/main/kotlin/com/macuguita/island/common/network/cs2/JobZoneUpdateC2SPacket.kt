package com.macuguita.island.common.network.cs2

import com.macuguita.island.common.Island
import com.macuguita.island.common.block.entity.JobZoneMasterBlockEntity
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block

data class JobZoneUpdateC2SPacket(
    val blockPos: BlockPos,
    val jobId: Identifier,
    val zonePos: BlockPos,
    val zoneSize: Vec3i,
    val showBoundingBox: Boolean
) : CustomPacketPayload {

    companion object {
        val JOB_ZONE_UPDATE_ID = Island.id("job_zone_update")
        val ID = CustomPacketPayload.Type<JobZoneUpdateC2SPacket>(JOB_ZONE_UPDATE_ID)

        val CODEC: StreamCodec<RegistryFriendlyByteBuf, JobZoneUpdateC2SPacket> =
            StreamCodec.composite(
                BlockPos.STREAM_CODEC,
                JobZoneUpdateC2SPacket::blockPos,
                Identifier.STREAM_CODEC,
                JobZoneUpdateC2SPacket::jobId,
                BlockPos.STREAM_CODEC,
                JobZoneUpdateC2SPacket::zonePos,
                Vec3i.STREAM_CODEC,
                JobZoneUpdateC2SPacket::zoneSize,
                ByteBufCodecs.BOOL,
                JobZoneUpdateC2SPacket::showBoundingBox,
                ::JobZoneUpdateC2SPacket
            )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    class Receiver() : ServerPlayNetworking.PlayPayloadHandler<JobZoneUpdateC2SPacket> {

        override fun receive(
            payload: JobZoneUpdateC2SPacket,
            context: ServerPlayNetworking.Context
        ) {
            val server = context.server()

            server.execute {
                val level = context.player().level()
                val blockEntityPos = payload.blockPos
                (level.getBlockEntity(blockEntityPos) as? JobZoneMasterBlockEntity)?.let { blockEntity ->
                    blockEntity.jobId = payload.jobId
                    blockEntity.zonePos = payload.zonePos
                    blockEntity.zoneSize = payload.zoneSize
                    blockEntity.showBoundingBox = payload.showBoundingBox

                    blockEntity.setChanged()
                    level.sendBlockUpdated(
                        blockEntityPos,
                        blockEntity.blockState,
                        blockEntity.blockState,
                        Block.UPDATE_ALL
                    )
                }
            }
        }

    }
}
