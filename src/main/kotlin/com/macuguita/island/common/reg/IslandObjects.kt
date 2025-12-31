/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.reg

import com.macuguita.island.common.CommonEntrypoint
import com.macuguita.lib.platform.registry.GuitaRegistries
import com.macuguita.lib.platform.registry.GuitaRegistry
import com.macuguita.lib.platform.registry.GuitaRegistryEntry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour


object IslandObjects {

    val BLOCKS = GuitaRegistries.create(BuiltInRegistries.BLOCK, CommonEntrypoint.MOD_ID)
    val ITEMS = GuitaRegistries.create(BuiltInRegistries.ITEM, CommonEntrypoint.MOD_ID)

    private fun <T : Block> registerWithItem(
        name: String,
        blockFactory: (BlockBehaviour.Properties) -> T,
        blockProperties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        blockItemFactory: ((Block, Item.Properties) -> BlockItem)? = ::BlockItem,
        blockReg: GuitaRegistry<Block> = BLOCKS,
        itemReg: GuitaRegistry<Item> = ITEMS
    ): GuitaRegistryEntry<T> = blockReg.register(name) {
        val block = blockFactory(blockProperties.setId(keyOfBlock(name)))

        if (blockItemFactory != null) {
            itemReg.register(name) {
                blockItemFactory(block, Item.Properties().useBlockDescriptionPrefix().setId(keyOfItem(name)))
            }
        }

        block
    }

    private fun <T : Item> registerItem(
        name: String,
        itemFactory: (Item.Properties) -> T,
        properties: Item.Properties,
        itemReg: GuitaRegistry<Item> = ITEMS
    ): GuitaRegistryEntry<T> = itemReg.register(name) { itemFactory(properties.setId(keyOfItem(name))) }

    private fun keyOfBlock(name: String): ResourceKey<Block> =
        ResourceKey.create(Registries.BLOCK, CommonEntrypoint.id(name))


    private fun keyOfItem(name: String): ResourceKey<Item> =
        ResourceKey.create(Registries.ITEM, CommonEntrypoint.id(name))

}
