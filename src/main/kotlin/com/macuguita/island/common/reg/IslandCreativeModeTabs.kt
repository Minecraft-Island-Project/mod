/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.reg

import com.macuguita.island.common.CommonEntrypoint
import com.macuguita.lib.platform.registry.GuitaRegistries
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object IslandCreativeModeTabs {

    val CREATIVE_MODE_TABS = GuitaRegistries.create(BuiltInRegistries.CREATIVE_MODE_TAB, CommonEntrypoint.MOD_ID)

    val ISLAND_TAB = CREATIVE_MODE_TABS.register("island") {
        CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("creative_tab.${CommonEntrypoint.MOD_ID}.island"))
            .icon { ItemStack(IslandObjects.SMALL_LOG_OAK_TABLE.get()) }
            .displayItems { _, output ->
                IslandObjects.BLOCK_ITEMS.stream().map { it.get().defaultInstance }.forEach(output::accept)
            }.build()
    }

    fun init() {
        CREATIVE_MODE_TABS.init()
    }
}
