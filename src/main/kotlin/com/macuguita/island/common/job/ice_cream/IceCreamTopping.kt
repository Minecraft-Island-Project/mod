/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.job.ice_cream

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import java.util.*

enum class IceCreamTopping : StringRepresentable {
    STRAWBERRY_SYRUP,
    CHOCOLATE_SYRUP,
    CARAMEL_SYRUP,
    SPRINKLES,
    NUTS,
    ;

    override fun getSerializedName(): String = name.lowercase(Locale.ROOT)

    companion object {
        @JvmStatic
        val CODEC: Codec<IceCreamTopping> = Codec.STRING.xmap(
            { str -> entries.first { it.getSerializedName() == str } },
            { it.getSerializedName() }
        )

        @JvmStatic
        val BY_ID = ByIdMap.continuous(
            { it.ordinal },
            IceCreamTopping.entries.toTypedArray(),
            ByIdMap.OutOfBoundsStrategy.ZERO
        )

        @JvmStatic
        val STREAM_CODEC: StreamCodec<ByteBuf, IceCreamTopping> =
            ByteBufCodecs.idMapper(BY_ID, { it.ordinal })
    }
}
