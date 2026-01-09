/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.reg

import com.macuguita.island.common.Island
import com.macuguita.island.common.data_components.IceCreamComponent
import com.macuguita.lib.reg.GuitaRegistries
import com.macuguita.lib.reg.GuitaRegistry
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries

object IslandDataComponents {

    val DATA_COMPONENTS: GuitaRegistry<DataComponentType<*>> =
        GuitaRegistries.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Island.MOD_ID)

    val ICE_CREAM_COMPONENT = DATA_COMPONENTS.register("ice_cream_component") {
        DataComponentType.builder<IceCreamComponent>().persistent(
            IceCreamComponent.CODEC
        ).build()
    }

    fun init() {
        DATA_COMPONENTS.init()
    }
}
