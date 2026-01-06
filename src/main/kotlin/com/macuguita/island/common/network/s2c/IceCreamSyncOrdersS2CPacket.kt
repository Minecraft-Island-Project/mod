package com.macuguita.island.common.network.s2c

import com.macuguita.island.client.ClientEntrypoint
import com.macuguita.island.common.Island
import com.macuguita.island.common.data_components.IceCreamComponent
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

data class IceCreamSyncOrdersS2CPacket(
    val orders: List<IceCreamComponent>
) : CustomPacketPayload {

    companion object {
        val ICE_CREAM_ORDERS_SYNC = Island.id("ice_cream_orders_sync")
        val ID = CustomPacketPayload.Type<IceCreamSyncOrdersS2CPacket>(ICE_CREAM_ORDERS_SYNC)

        val CODEC: StreamCodec<RegistryFriendlyByteBuf, IceCreamSyncOrdersS2CPacket> =
            StreamCodec.composite(
                ByteBufCodecs.collection(
                    { ArrayList() },
                    IceCreamComponent.STREAM_CODEC
                ),
                IceCreamSyncOrdersS2CPacket::orders,
                ::IceCreamSyncOrdersS2CPacket
            )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    class Receiver() : ClientPlayNetworking.PlayPayloadHandler<IceCreamSyncOrdersS2CPacket> {

        override fun receive(
            payload: IceCreamSyncOrdersS2CPacket,
            context: ClientPlayNetworking.Context
        ) {
            ClientEntrypoint.iceCreamOrders = payload.orders
        }

    }
}
