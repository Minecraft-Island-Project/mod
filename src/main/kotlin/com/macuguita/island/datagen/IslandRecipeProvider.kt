/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.datagen

import com.macuguita.island.common.reg.IslandObjects
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture

class IslandRecipeProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricRecipeProvider(output, registriesFuture) {
    override fun createRecipeProvider(
        holderLookup: HolderLookup.Provider,
        recipeOutput: RecipeOutput
    ): RecipeProvider = object : RecipeProvider(holderLookup, recipeOutput) {
        override fun buildRecipes() {
            IslandObjects.BEAMS.entries.forEach { entry ->
                val block = entry.get()
                val wood = IslandObjects.BLOCK_TO_WOOD[block]!!
                createBeamRecipe(recipeOutput, block, wood.getLog())
            }
        }

        private fun createBeamRecipe(exporter: RecipeOutput, carvedLog: Block, log: Block) =
            ShapedRecipeBuilder.shaped(BuiltInRegistries.ITEM, RecipeCategory.DECORATIONS, carvedLog, 12)
                .pattern("#")
                .pattern("#")
                .pattern("#")
                .define('#', log)
                .unlockedBy(getHasName(log), has(log))
                .save(exporter)
    }

    override fun getName(): String = "Island recipe provider"
}
