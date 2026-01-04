/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.data_components

import com.macuguita.island.common.Island.orNull
import com.macuguita.island.common.job.ice_cream.IceCreamFlavour
import com.macuguita.island.common.job.ice_cream.IceCreamTopping
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import java.util.*

@JvmRecord
data class IceCreamComponent(
    val firstFlavour: IceCreamFlavour,
    val secondFlavour: IceCreamFlavour?,
    val thirdFlavour: IceCreamFlavour?,
    val topping: IceCreamTopping?,
) {

    fun isFull(): Boolean = secondFlavour != null && thirdFlavour != null
    fun asSequenceOfFlavours(): Sequence<IceCreamFlavour> =
        sequenceOf(firstFlavour, secondFlavour, thirdFlavour).filterNotNull()

    fun toListOfFlavours(): List<IceCreamFlavour> = asSequenceOfFlavours().toList()

    companion object {
        @JvmStatic
        val CODEC: Codec<IceCreamComponent> = RecordCodecBuilder.create { i ->
            i.group(
                IceCreamFlavour.CODEC.fieldOf("first_flavour")
                    .forGetter { c: IceCreamComponent -> c.firstFlavour },
                IceCreamFlavour.CODEC.optionalFieldOf("second_flavour")
                    .forGetter { c: IceCreamComponent -> Optional.ofNullable(c.secondFlavour) },
                IceCreamFlavour.CODEC.optionalFieldOf("third_flavour")
                    .forGetter { c: IceCreamComponent -> Optional.ofNullable(c.thirdFlavour) },
                IceCreamTopping.CODEC.optionalFieldOf("topping")
                    .forGetter { c: IceCreamComponent -> Optional.ofNullable(c.topping) },
            ).apply(i) { first, second, third, topping ->
                IceCreamComponent(first, second.orNull(), third.orNull(), topping.orNull())
            }
        }

        @JvmStatic
        fun fromList(flavours: List<IceCreamFlavour>, topping: IceCreamTopping? = null): IceCreamComponent {
            require(flavours.isNotEmpty()) {
                "IceCreamFlavoursComponent requires at least one flavour"
            }

            return IceCreamComponent(
                firstFlavour = flavours[0],
                secondFlavour = flavours.getOrNull(1),
                thirdFlavour = flavours.getOrNull(2),
                topping = topping,
            )
        }
    }
}
