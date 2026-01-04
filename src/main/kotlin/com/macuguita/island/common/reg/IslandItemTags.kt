/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.reg

import com.macuguita.island.common.Island
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object IslandItemTags {

    val SECATEURS = createTag("secateurs")
    val ICE_CREAM_HOLDER = createTag("ice_cream_holder")
    val JOB_AREA = createTag("job_area")

    fun createTag(name: String): TagKey<Item> = TagKey.create(Registries.ITEM, Island.id(name))
}
