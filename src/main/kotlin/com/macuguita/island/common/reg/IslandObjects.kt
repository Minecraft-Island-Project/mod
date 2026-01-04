/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.reg

import com.macuguita.island.common.Island
import com.macuguita.island.common.block.DeskBlock
import com.macuguita.island.common.block.ResizableBeamBlock
import com.macuguita.island.common.util.Wood
import com.macuguita.island.common.util.WoodSet
import com.macuguita.island.common.util.WoodType
import com.macuguita.island.mixin.FireBlockAccessor
import com.macuguita.lib.platform.registry.GuitaRegistries
import com.macuguita.lib.platform.registry.GuitaRegistry
import com.macuguita.lib.platform.registry.GuitaRegistryEntry
import net.fabricmc.fabric.api.registry.FuelRegistryEvents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour


object IslandObjects {

    val BLOCKS: GuitaRegistry<Block> = GuitaRegistries.create(BuiltInRegistries.BLOCK, Island.MOD_ID)
    val BLOCK_ITEMS: GuitaRegistry<Item> = GuitaRegistries.create(BuiltInRegistries.ITEM, Island.MOD_ID)
    val ITEMS: GuitaRegistry<Item> = GuitaRegistries.create(BuiltInRegistries.ITEM, Island.MOD_ID)

    val FURNITURE: GuitaRegistry<Block> = GuitaRegistries.create(BLOCKS)

    val WOODS: GuitaRegistry<Block> = GuitaRegistries.create(BLOCKS)
    val BEAMS: GuitaRegistry<Block> = GuitaRegistries.create(WOODS)

    val BLOCK_TO_WOOD = mutableMapOf<Block, WoodType>()
    val WOOD_SETS = mutableMapOf<WoodType, WoodSet>()

    /*------------------------*/
    /* BLOCKS & ITEMS         */
    /*------------------------*/

    val SECATEURS = registerItem("secateurs", ::Item)
    val SMALL_LOG_OAK_TABLE = registerWithItem("small_log_oak_table", ::Block, blockReg = FURNITURE)
    val DESK = registerWithItem("desk", ::DeskBlock, blockReg = FURNITURE)

    private fun <T : Block> registerWithItem(
        name: String,
        blockFactory: (BlockBehaviour.Properties) -> T,
        blockProperties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        blockItemFactory: ((Block, Item.Properties) -> BlockItem)? = ::BlockItem,
        blockReg: GuitaRegistry<Block> = BLOCKS,
        itemReg: GuitaRegistry<Item> = BLOCK_ITEMS
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
        properties: Item.Properties = Item.Properties(),
        itemReg: GuitaRegistry<Item> = ITEMS
    ): GuitaRegistryEntry<T> = itemReg.register(name) { itemFactory(properties.setId(keyOfItem(name))) }

    private fun keyOfBlock(name: String): ResourceKey<Block> =
        ResourceKey.create(Registries.BLOCK, Island.id(name))


    private fun keyOfItem(name: String): ResourceKey<Item> =
        ResourceKey.create(Registries.ITEM, Island.id(name))

    fun init() {
        BLOCKS.init()
        BLOCK_ITEMS.init()

        for (w in Wood.entries) {
            val beam = registerWithItem("${w.blockName}_beam", ::ResizableBeamBlock, blockReg = BEAMS)
            val strippedBeam =
                registerWithItem("${w.getStripped().blockName}_beam", ::ResizableBeamBlock, blockReg = BEAMS)
            ResizableBeamBlock.STRIPPED_BEAM_BLOCKS[beam.get()] = strippedBeam.get()
            BLOCK_TO_WOOD[beam.get()] = w
            BLOCK_TO_WOOD[strippedBeam.get()] = w.getStripped()

            WOOD_SETS[w] = WoodSet(
                beam = beam.get()
            )
            WOOD_SETS[w.getStripped()] = WoodSet(
                beam = strippedBeam.get()
            )

            if (w.burns()) {
                FuelRegistryEvents.BUILD.register(FuelRegistryEvents.BuildCallback { builder, _ ->
                    builder.add(beam.get(), 75)
                    builder.add(strippedBeam.get(), 75)
                })
                (Blocks.FIRE as FireBlockAccessor).`island$registerFlammableBlock`(beam.get(), 5, 5)
                (Blocks.FIRE as FireBlockAccessor).`island$registerFlammableBlock`(strippedBeam.get(), 5, 5)
            }
        }
    }
}
