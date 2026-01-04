/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.reg

import com.macuguita.island.common.Island
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object IslandBlockTags {

    val BEAM = createTag("beam")

    fun createTag(name: String): TagKey<Block> = TagKey.create(Registries.BLOCK, Island.id(name))
}
