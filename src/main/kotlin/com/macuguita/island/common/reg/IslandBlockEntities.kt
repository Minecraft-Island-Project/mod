package com.macuguita.island.common.reg

import com.macuguita.island.common.Island
import com.macuguita.island.common.block.entity.JobZoneMasterBlockEntity
import com.macuguita.lib.platform.registry.GuitaRegistries
import com.macuguita.lib.platform.registry.GuitaRegistry
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType

object IslandBlockEntities {

    val BLOCK_ENTITY_TYPES: GuitaRegistry<BlockEntityType<*>> =
        GuitaRegistries.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Island.MOD_ID)

    val JOB_ZONE_MASTER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("job_zone_master_block_entity", {
        FabricBlockEntityTypeBuilder.create(
            ::JobZoneMasterBlockEntity,
            IslandObjects.JOB_ZONE_MASTER.get()
        ).build()
    })

    fun init() {
        BLOCK_ENTITY_TYPES.init()
    }
}
