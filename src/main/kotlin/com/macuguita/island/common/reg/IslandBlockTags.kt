package com.macuguita.island.common.reg

import com.macuguita.island.common.CommonEntrypoint
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object IslandBlockTags {

    val BEAM = createTag("beam")

    fun createTag(name: String): TagKey<Block> = TagKey.create(Registries.BLOCK, CommonEntrypoint.id(name))
}
