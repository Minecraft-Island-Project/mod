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

enum class IceCreamFlavour : StringRepresentable {
    VANILLA,
    CHOCOLATE,
    STRAWBERRY,
    MINT_CHOCOLATE_CHIP,
    MANGO,
    COOKIES_AND_CREAM,
    ;

    override fun getSerializedName(): String = name.lowercase(Locale.ROOT)

    companion object {
        @JvmStatic
        val CODEC: Codec<IceCreamFlavour> = Codec.STRING.xmap(
            { str -> entries.first { it.getSerializedName() == str } },
            { it.getSerializedName() }
        )

        @JvmStatic
        val BY_ID = ByIdMap.continuous(
            { it.ordinal },
            entries.toTypedArray(),
            ByIdMap.OutOfBoundsStrategy.ZERO
        )

        @JvmStatic
        val STREAM_CODEC: StreamCodec<ByteBuf, IceCreamFlavour> =
            ByteBufCodecs.idMapper(BY_ID, { it.ordinal })
    }
}
