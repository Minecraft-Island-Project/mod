/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.mixin.datagen;

import net.minecraft.client.data.models.BlockModelGenerators;

import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.renderer.block.model.VariantMutator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockModelGenerators.class)
public interface BlockModelGeneratorsMixin {

	@Accessor("ROTATION_HORIZONTAL_FACING")
	PropertyDispatch<VariantMutator> island$getROTATION_HORIZONTAL_FACING();
}
