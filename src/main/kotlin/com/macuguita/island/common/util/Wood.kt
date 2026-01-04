/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.util

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

enum class Wood(val blockName: String, val plankBlock: Block, val logBlock: Block) : WoodType {
    OAK("oak", Blocks.OAK_PLANKS, Blocks.OAK_LOG),
    SPRUCE("spruce", Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_LOG),
    BIRCH("birch", Blocks.BIRCH_PLANKS, Blocks.BIRCH_LOG),
    JUNGLE("jungle", Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_LOG),
    ACACIA("acacia", Blocks.ACACIA_PLANKS, Blocks.ACACIA_LOG),
    DARK_OAK("dark_oak", Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_LOG),
    MANGROVE("mangrove", Blocks.MANGROVE_PLANKS, Blocks.MANGROVE_LOG),
    CHERRY("cherry", Blocks.CHERRY_PLANKS, Blocks.CHERRY_LOG),
    BAMBOO("bamboo", Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_BLOCK),
    PALE_OAK("pale_oak", Blocks.PALE_OAK_PLANKS, Blocks.PALE_OAK_LOG),
    CRIMSON("crimson", Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_STEM),
    WARPED("warped", Blocks.WARPED_PLANKS, Blocks.WARPED_STEM),
    ;

    override fun getName(): String = this.name

    override fun getPlank(): Block = this.plankBlock

    override fun getLog(): Block = this.logBlock

    override fun isStripped(): Boolean = false

    fun getStripped(): StrippedWood {
        return when (this) {
            OAK -> StrippedWood.STRIPPED_OAK
            SPRUCE -> StrippedWood.STRIPPED_SPRUCE
            BIRCH -> StrippedWood.STRIPPED_BIRCH
            JUNGLE -> StrippedWood.STRIPPED_JUNGLE
            ACACIA -> StrippedWood.STRIPPED_ACACIA
            DARK_OAK -> StrippedWood.STRIPPED_DARK_OAK
            MANGROVE -> StrippedWood.STRIPPED_MANGROVE
            CHERRY -> StrippedWood.STRIPPED_CHERRY
            BAMBOO -> StrippedWood.STRIPPED_BAMBOO
            PALE_OAK -> StrippedWood.STRIPPED_PALE_OAK
            CRIMSON -> StrippedWood.STRIPPED_CRIMSON
            WARPED -> StrippedWood.STRIPPED_WARPED
        }
    }

    fun burns(): Boolean {
        return when (this) {
            CRIMSON, WARPED -> true
            else -> false
        }
    }
}
