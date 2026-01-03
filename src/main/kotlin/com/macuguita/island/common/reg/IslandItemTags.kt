package com.macuguita.island.common.reg

import com.macuguita.island.common.CommonEntrypoint
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object IslandItemTags {

    val SECATEURS = createTag("secateurs")

    fun createTag(name: String): TagKey<Item> = TagKey.create(Registries.ITEM, CommonEntrypoint.id(name))
}
