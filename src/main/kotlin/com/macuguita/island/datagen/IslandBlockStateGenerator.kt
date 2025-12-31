/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.datagen

import com.macuguita.island.common.CommonEntrypoint
import com.macuguita.island.common.reg.IslandObjects
import com.macuguita.island.mixin.datagen.BlockModelGeneratorsMixin
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.world.level.block.Block
import java.util.*


class IslandBlockStateGenerator(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockModelGenerators: BlockModelGenerators) {
        registerSmallLogTable(
            blockModelGenerators, IslandObjects.SMALL_LOG_OAK_TABLE.get(),
            TextureMapping().put(
                TextureSlot.ALL,
                TextureMapping.getBlockTexture(IslandObjects.SMALL_LOG_OAK_TABLE.get())
            )
        )
        registerDesk(
            blockModelGenerators, IslandObjects.DESK.get(),
            TextureMapping().put(
                TextureSlot.ALL,
                TextureMapping.getBlockTexture(IslandObjects.DESK.get())
            )
        )
    }

    override fun generateItemModels(blockModelGenerators: ItemModelGenerators) {
    }

    private fun registerSmallLogTable(blockModelGenerators: BlockModelGenerators, block: Block, tm: TextureMapping) {
        val id = SMALL_LOG_TABLE.create(
            block,
            tm,
            blockModelGenerators.modelOutput
        )
        val multiVariant = BlockModelGenerators.plainVariant(id)
        blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, multiVariant))
        blockModelGenerators.registerSimpleItemModel(block, id)
    }

    private fun registerDesk(blockModelGenerators: BlockModelGenerators, block: Block, tm: TextureMapping) {
        val id = VANITY.create(
            block,
            tm,
            blockModelGenerators.modelOutput
        )
        val multiVariant = BlockModelGenerators.plainVariant(id)
        blockModelGenerators.blockStateOutput.accept(
            BlockModelGenerators.createSimpleBlock(block, multiVariant)
                .with((blockModelGenerators as BlockModelGeneratorsMixin).`island$getROTATION_HORIZONTAL_FACING`())
        )
        blockModelGenerators.registerSimpleItemModel(block, id)
    }

    companion object {
        private val SMALL_LOG_TABLE = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/small_log_table")),
            Optional.empty(),
            TextureSlot.ALL
        )

        private val VANITY = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/vanity")),
            Optional.empty(),
            TextureSlot.ALL
        )
    }

}
