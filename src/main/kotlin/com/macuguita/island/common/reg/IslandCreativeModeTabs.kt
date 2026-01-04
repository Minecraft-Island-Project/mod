/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.reg

import com.macuguita.island.common.Island
import com.macuguita.island.common.util.Wood
import com.macuguita.lib.platform.registry.GuitaRegistries
import com.macuguita.lib.platform.registry.GuitaRegistry
import com.macuguita.lib.platform.registry.GuitaRegistryEntry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

object IslandCreativeModeTabs {

    val CREATIVE_MODE_TABS: GuitaRegistry<CreativeModeTab> =
        GuitaRegistries.create(BuiltInRegistries.CREATIVE_MODE_TAB, Island.MOD_ID)

    val FURNITURE: GuitaRegistryEntry<CreativeModeTab> = CREATIVE_MODE_TABS.register("furniture") {
        CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("creative_tab.${Island.MOD_ID}.furniture"))
            .icon { ItemStack(IslandObjects.SMALL_LOG_OAK_TABLE.get()) }
            .displayItems { _, output ->
                IslandObjects.FURNITURE.stream().map { it.get().asItem().defaultInstance }.forEach(output::accept)
            }.build()
    }

    val WOODS: GuitaRegistryEntry<CreativeModeTab> = CREATIVE_MODE_TABS.register("woods") {
        CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("creative_tab.${Island.MOD_ID}.woods"))
            .icon { ItemStack(IslandObjects.WOOD_SETS[Wood.OAK]!!.beam) }
            .displayItems { _, output ->
                output.accept(IslandObjects.SECATEURS.get())
                IslandObjects.WOODS.stream().map { it.get().asItem().defaultInstance }.forEach(output::accept)
            }.build()
    }

    fun init() {
        CREATIVE_MODE_TABS.init()
    }
}
