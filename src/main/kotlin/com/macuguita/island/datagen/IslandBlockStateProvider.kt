/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.datagen

import com.macuguita.island.common.CommonEntrypoint
import com.macuguita.island.common.block.ResizableBeamBlock
import com.macuguita.island.common.reg.IslandObjects
import com.macuguita.island.mixin.datagen.BlockModelGeneratorsMixin
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.ConditionBuilder
import net.minecraft.client.data.models.blockstates.MultiPartGenerator
import net.minecraft.client.data.models.model.ModelTemplate
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.client.renderer.block.model.VariantMutator
import net.minecraft.client.renderer.block.model.multipart.CombinedCondition
import net.minecraft.client.renderer.block.model.multipart.Condition
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BooleanProperty
import java.util.*
import java.util.stream.Stream


class IslandBlockStateProvider(output: FabricDataOutput) : FabricModelProvider(output) {

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

        IslandObjects.BEAMS.entries.forEach { entry ->
            val block = entry.get()
            val wood = IslandObjects.COMBINED_WOOD_MAP[block]!!
            registerBeamBlock(
                blockModelGenerators, block,
                TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(wood.getLog())),
                TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(wood.getLog()))
                    .put(TextureSlot.TOP, getBeamTexture(block, "_top_2x2")),
                TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(wood.getLog()))
                    .put(TextureSlot.TOP, getBeamTexture(block, "_top_4x4")),
                TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(wood.getLog()))
                    .put(TextureSlot.TOP, getBeamTexture(block, "_top_6x6")),
                TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(wood.getLog()))
                    .put(TextureSlot.TOP, getBeamTexture(block, "_top_8x8")),
                TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(wood.getLog()))
                    .put(TextureSlot.TOP, getBeamTexture(block, "_top_10x10")),
                TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(wood.getLog()))
                    .put(TextureSlot.TOP, getBeamTexture(block, "_top_12x12")),
                TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(wood.getLog()))
                    .put(TextureSlot.TOP, getBeamTexture(block, "_top_14x14"))
            )
        }
    }

    override fun generateItemModels(blockModelGenerators: ItemModelGenerators) {
    }

    private fun registerBeamBlock(
        blockStateModelGenerator: BlockModelGenerators, block: Block,
        tmCore: TextureMapping, tmSide2x2: TextureMapping, tmSide4x4: TextureMapping, tmSide6x6: TextureMapping,
        tmSide8x8: TextureMapping, tmSide10x10: TextureMapping, tmSide12x12: TextureMapping, tmSide14x14: TextureMapping
    ) {
        val textureMapSideMap = mapOf(
            1 to tmSide2x2,
            2 to tmSide4x4,
            3 to tmSide6x6,
            4 to tmSide8x8,
            5 to tmSide10x10,
            6 to tmSide12x12,
            7 to tmSide14x14
        )
        val weightedVariantCore2 =
            BlockModelGenerators.plainVariant(BEAM_CORE_2X2.create(block, tmCore, blockStateModelGenerator.modelOutput))
        val weightedVariantCore4 =
            BlockModelGenerators.plainVariant(BEAM_CORE_4X4.create(block, tmCore, blockStateModelGenerator.modelOutput))
        val weightedVariantCore6 =
            BlockModelGenerators.plainVariant(BEAM_CORE_6X6.create(block, tmCore, blockStateModelGenerator.modelOutput))
        val weightedVariantCore8 =
            BlockModelGenerators.plainVariant(BEAM_CORE_8X8.create(block, tmCore, blockStateModelGenerator.modelOutput))
        val weightedVariantCore10 = BlockModelGenerators.plainVariant(
            BEAM_CORE_10X10.create(
                block,
                tmCore,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantCore12 = BlockModelGenerators.plainVariant(
            BEAM_CORE_12X12.create(
                block,
                tmCore,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantCore14 = BlockModelGenerators.plainVariant(
            BEAM_CORE_14X14.create(
                block,
                tmCore,
                blockStateModelGenerator.modelOutput
            )
        )
        val coreWeightedVariantMap = mapOf(
            1 to weightedVariantCore2,
            2 to weightedVariantCore4,
            3 to weightedVariantCore6,
            4 to weightedVariantCore8,
            5 to weightedVariantCore10,
            6 to weightedVariantCore12,
            7 to weightedVariantCore14
        )
        val weightedVariantSideUp2 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_UP_2X2.createWithSuffix(
                block,
                "_up",
                textureMapSideMap[1]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideUp4 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_UP_4X4.createWithSuffix(
                block,
                "_up",
                textureMapSideMap[2]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideUp6 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_UP_6X6.createWithSuffix(
                block,
                "_up",
                textureMapSideMap[3]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideUp8 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_UP_8X8.createWithSuffix(
                block,
                "_up",
                textureMapSideMap[4]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideUp10 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_UP_10X10.createWithSuffix(
                block,
                "_up",
                textureMapSideMap[5]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideUp12 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_UP_12X12.createWithSuffix(
                block,
                "_up",
                textureMapSideMap[6]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideUp14 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_UP_14X14.createWithSuffix(
                block,
                "_up",
                textureMapSideMap[7]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val sideUpWeightedVariantMap = mapOf(
            1 to weightedVariantSideUp2,
            2 to weightedVariantSideUp4,
            3 to weightedVariantSideUp6,
            4 to weightedVariantSideUp8,
            5 to weightedVariantSideUp10,
            6 to weightedVariantSideUp12,
            7 to weightedVariantSideUp14
        )

        val weightedVariantSideDown2 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_DOWN_2X2.createWithSuffix(
                block,
                "_down",
                textureMapSideMap[1]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideDown4 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_DOWN_4X4.createWithSuffix(
                block,
                "_down",
                textureMapSideMap[2]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideDown6 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_DOWN_6X6.createWithSuffix(
                block,
                "_down",
                textureMapSideMap[3]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideDown8 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_DOWN_8X8.createWithSuffix(
                block,
                "_down",
                textureMapSideMap[4]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideDown10 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_DOWN_10X10.createWithSuffix(
                block,
                "_down",
                textureMapSideMap[5]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideDown12 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_DOWN_12X12.createWithSuffix(
                block,
                "_down",
                textureMapSideMap[6]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val weightedVariantSideDown14 = BlockModelGenerators.plainVariant(
            BEAM_SIDE_DOWN_14X14.createWithSuffix(
                block,
                "_down",
                textureMapSideMap[7]!!,
                blockStateModelGenerator.modelOutput
            )
        )
        val sideDownWeightedVariantMap = mapOf(
            1 to weightedVariantSideDown2,
            2 to weightedVariantSideDown4,
            3 to weightedVariantSideDown6,
            4 to weightedVariantSideDown8,
            5 to weightedVariantSideDown10,
            6 to weightedVariantSideDown12,
            7 to weightedVariantSideDown14
        )

        val multipartBlockModelDefinitionCreator = MultiPartGenerator.multiPart(block)

        for (size in ResizableBeamBlock.RADIUS.possibleValues) {
            generateRotatedCoreModels(multipartBlockModelDefinitionCreator, coreWeightedVariantMap, size)
            for (dir in Direction.entries) {
                val sideProp: BooleanProperty = ResizableBeamBlock.PROPERTY_BY_DIRECTION[dir]!!
                multipartBlockModelDefinitionCreator.with(
                    BlockModelGenerators.condition()
                        .term(ResizableBeamBlock.RADIUS, size)
                        .term(sideProp, true),
                    getSidedModel(sideUpWeightedVariantMap[size]!!, sideDownWeightedVariantMap[size]!!, dir).with(
                        getModelRotation(dir)
                    )
                )
            }
        }

        blockStateModelGenerator.blockStateOutput.accept(multipartBlockModelDefinitionCreator)

        val inventoryModel: Identifier =
            BEAM_SIDE_INVENTORY.create(block, tmSide8x8, blockStateModelGenerator.modelOutput)
        blockStateModelGenerator.registerSimpleItemModel(block, inventoryModel)
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
        fun getBeamTexture(block: Block, suffix: String) =
            BuiltInRegistries.BLOCK.getKey(block).withPath { "block/beams/$it$suffix" }

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

        private val BEAM_SIDE_INVENTORY: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/template_beam_inventory")),
            Optional.empty(),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_CORE_2X2: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/cores/template_beam_core_2x2")),
            Optional.of("_core_2x2"),
            TextureSlot.SIDE
        )

        private val BEAM_CORE_4X4: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/cores/template_beam_core_4x4")),
            Optional.of("_core_4x4"),
            TextureSlot.SIDE
        )

        private val BEAM_CORE_6X6: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/cores/template_beam_core_6x6")),
            Optional.of("_core_6x6"),
            TextureSlot.SIDE
        )

        private val BEAM_CORE_8X8: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/cores/template_beam_core_8x8")),
            Optional.of("_core_8x8"),
            TextureSlot.SIDE
        )

        private val BEAM_CORE_10X10: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/cores/template_beam_core_10x10")),
            Optional.of("_core_10x10"),
            TextureSlot.SIDE
        )

        private val BEAM_CORE_12X12: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/cores/template_beam_core_12x12")),
            Optional.of("_core_12x12"),
            TextureSlot.SIDE
        )

        private val BEAM_CORE_14X14: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/cores/template_beam_core_14x14")),
            Optional.of("_core_14x14"),
            TextureSlot.SIDE
        )

        private val BEAM_SIDE_UP_2X2: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_up/template_beam_side_2x2")),
            Optional.of("_side_2x2"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_UP_4X4: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_up/template_beam_side_4x4")),
            Optional.of("_side_4x4"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_UP_6X6: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_up/template_beam_side_6x6")),
            Optional.of("_side_6x6"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_UP_8X8: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_up/template_beam_side_8x8")),
            Optional.of("_side_8x8"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_UP_10X10: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_up/template_beam_side_10x10")),
            Optional.of("_side_10x10"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_UP_12X12: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_up/template_beam_side_12x12")),
            Optional.of("_side_12x12"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_UP_14X14: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_up/template_beam_side_14x14")),
            Optional.of("_side_14x14"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_DOWN_2X2: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_down/template_beam_side_2x2")),
            Optional.of("_side_2x2"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_DOWN_4X4: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_down/template_beam_side_4x4")),
            Optional.of("_side_4x4"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_DOWN_6X6: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_down/template_beam_side_6x6")),
            Optional.of("_side_6x6"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_DOWN_8X8: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_down/template_beam_side_8x8")),
            Optional.of("_side_8x8"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_DOWN_10X10: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_down/template_beam_side_10x10")),
            Optional.of("_side_10x10"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_DOWN_12X12: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_down/template_beam_side_12x12")),
            Optional.of("_side_12x12"),
            TextureSlot.SIDE, TextureSlot.TOP
        )

        private val BEAM_SIDE_DOWN_14X14: ModelTemplate = ModelTemplate(
            Optional.of(CommonEntrypoint.id("custom/beams/sides_down/template_beam_side_14x14")),
            Optional.of("_side_14x14"),
            TextureSlot.SIDE, TextureSlot.TOP
        )
    }

    private fun generateRotatedCoreModels(
        multiPartGenerator: MultiPartGenerator,
        coreWeightedVariantMap: Map<Int, MultiVariant>,
        size: Int
    ) {
        multiPartGenerator.with(
            and(
                BlockModelGenerators.condition().term(ResizableBeamBlock.RADIUS, size),
                or(
                    BlockModelGenerators.condition().term(ResizableBeamBlock.UP, false),
                    BlockModelGenerators.condition().term(ResizableBeamBlock.DOWN, false),
                    BlockModelGenerators.condition().term(ResizableBeamBlock.NORTH, false),
                    BlockModelGenerators.condition().term(ResizableBeamBlock.SOUTH, false),
                    BlockModelGenerators.condition().term(ResizableBeamBlock.EAST, false),
                    BlockModelGenerators.condition().term(ResizableBeamBlock.WEST, false)
                ),
                or(
                    BlockModelGenerators.condition().term(ResizableBeamBlock.UP, true),
                    BlockModelGenerators.condition().term(ResizableBeamBlock.DOWN, true),
                    and(
                        BlockModelGenerators.condition().term(ResizableBeamBlock.UP, false),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.DOWN, false),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.NORTH, false),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.SOUTH, false),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.EAST, false),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.WEST, false)
                    ),
                    and(
                        BlockModelGenerators.condition().term(ResizableBeamBlock.UP, false),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.DOWN, false),
                        or(
                            BlockModelGenerators.condition().term(ResizableBeamBlock.NORTH, false),
                            BlockModelGenerators.condition().term(ResizableBeamBlock.SOUTH, false)
                        ),
                        or(
                            BlockModelGenerators.condition().term(ResizableBeamBlock.WEST, false),
                            BlockModelGenerators.condition().term(ResizableBeamBlock.EAST, false)
                        )
                    ),
                    and(
                        BlockModelGenerators.condition().term(ResizableBeamBlock.UP, false),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.DOWN, false),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.NORTH, true),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.SOUTH, true),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.EAST, true),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.WEST, true)
                    )
                )
            ), coreWeightedVariantMap[size]!!
        )

        multiPartGenerator.with(
            and(
                BlockModelGenerators.condition().term(ResizableBeamBlock.RADIUS, size),
                BlockModelGenerators.condition().term(ResizableBeamBlock.UP, false),
                BlockModelGenerators.condition().term(ResizableBeamBlock.DOWN, false),
                and(
                    and(
                        BlockModelGenerators.condition().term(ResizableBeamBlock.NORTH, true),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.SOUTH, true)
                    ),
                    or(
                        BlockModelGenerators.condition().term(ResizableBeamBlock.EAST, false),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.WEST, false)
                    )
                )
            ), coreWeightedVariantMap.get(size)!!.with(BlockModelGenerators.X_ROT_90)
        )

        multiPartGenerator.with(
            and(
                BlockModelGenerators.condition().term(ResizableBeamBlock.RADIUS, size),
                BlockModelGenerators.condition().term(ResizableBeamBlock.UP, false),
                BlockModelGenerators.condition().term(ResizableBeamBlock.DOWN, false),
                and(
                    and(
                        BlockModelGenerators.condition().term(ResizableBeamBlock.EAST, true),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.WEST, true)
                    ),
                    or(
                        BlockModelGenerators.condition().term(ResizableBeamBlock.NORTH, false),
                        BlockModelGenerators.condition().term(ResizableBeamBlock.SOUTH, false)
                    )
                )
            ),
            coreWeightedVariantMap.get(size)!!.with(BlockModelGenerators.X_ROT_90.then(BlockModelGenerators.Y_ROT_90))
        )
    }

    fun and(vararg conditions: Any): Condition {
        return combine(CombinedCondition.Operation.AND, *conditions)
    }

    fun or(vararg conditions: Any): Condition {
        return combine(CombinedCondition.Operation.OR, *conditions)
    }

    private fun combine(op: CombinedCondition.Operation, vararg conditions: Any): Condition {
        val built = Stream.of(*conditions)
            .map<Condition?> { condition: Any? ->
                when (condition) {
                    is ConditionBuilder -> {
                        return@map condition.build()
                    }

                    is Condition -> {
                        return@map condition
                    }

                    else -> {
                        throw IllegalArgumentException("Unsupported condition type: " + condition!!.javaClass)
                    }
                }
            }
            .toList()
        return CombinedCondition(op, built as List<Condition>)
    }

    private fun getSidedModel(sideUp: MultiVariant, sideDown: MultiVariant, dir: Direction): MultiVariant {
        return when (dir) {
            Direction.UP, Direction.NORTH, Direction.EAST -> sideUp
            Direction.DOWN, Direction.SOUTH, Direction.WEST -> sideDown
        }
    }

    private fun getModelRotation(dir: Direction): VariantMutator {
        return when (dir) {
            Direction.EAST -> BlockModelGenerators.Y_ROT_90
            Direction.SOUTH -> BlockModelGenerators.Y_ROT_180
            Direction.WEST -> BlockModelGenerators.Y_ROT_270
            Direction.UP -> BlockModelGenerators.X_ROT_270
            Direction.DOWN -> BlockModelGenerators.X_ROT_90
            else -> BlockModelGenerators.NOP
        }
    }
}
