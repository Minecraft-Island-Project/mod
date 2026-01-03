package com.macuguita.island.common.util

import com.macuguita.island.common.util.Wood.*
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

enum class StrippedWood(val blockName: String, val logBlock: Block) : WoodType {
    STRIPPED_OAK("stripped_oak", Blocks.STRIPPED_OAK_LOG),
    STRIPPED_SPRUCE("stripped_spruce", Blocks.STRIPPED_SPRUCE_LOG),
    STRIPPED_BIRCH("stripped_birch", Blocks.STRIPPED_BIRCH_LOG),
    STRIPPED_JUNGLE("stripped_jungle", Blocks.STRIPPED_JUNGLE_LOG),
    STRIPPED_ACACIA("stripped_acacia", Blocks.STRIPPED_ACACIA_LOG),
    STRIPPED_DARK_OAK("stripped_dark_oak", Blocks.STRIPPED_DARK_OAK_LOG),
    STRIPPED_MANGROVE("stripped_mangrove", Blocks.STRIPPED_MANGROVE_LOG),
    STRIPPED_CHERRY("stripped_cherry", Blocks.STRIPPED_CHERRY_LOG),
    STRIPPED_BAMBOO("stripped_bamboo", Blocks.STRIPPED_BAMBOO_BLOCK),
    STRIPPED_PALE_OAK("stripped_pale_oak", Blocks.STRIPPED_PALE_OAK_LOG),
    STRIPPED_CRIMSON("stripped_crimson", Blocks.STRIPPED_CRIMSON_STEM),
    STRIPPED_WARPED("stripped_warped", Blocks.STRIPPED_WARPED_STEM),
    ;

    override fun getName(): String = this.name

    override fun getPlank(): Block? = null

    override fun getLog(): Block = this.logBlock

    override fun isStripped(): Boolean = true

    fun getRegular(): Wood {
        return when (this) {
            STRIPPED_OAK -> OAK
            STRIPPED_SPRUCE -> SPRUCE
            STRIPPED_BIRCH -> BIRCH
            STRIPPED_JUNGLE -> JUNGLE
            STRIPPED_ACACIA -> ACACIA
            STRIPPED_DARK_OAK -> DARK_OAK
            STRIPPED_MANGROVE -> MANGROVE
            STRIPPED_CHERRY -> CHERRY
            STRIPPED_BAMBOO -> BAMBOO
            STRIPPED_PALE_OAK -> PALE_OAK
            STRIPPED_CRIMSON -> CRIMSON
            STRIPPED_WARPED -> WARPED
        }
    }

    fun burns(): Boolean {
        return when (this) {
            STRIPPED_CRIMSON, STRIPPED_WARPED -> true
            else -> false
        }
    }
}
