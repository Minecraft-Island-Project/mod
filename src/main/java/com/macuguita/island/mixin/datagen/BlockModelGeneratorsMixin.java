/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.mixin.datagen;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.renderer.block.model.VariantMutator;

@Mixin(BlockModelGenerators.class)
public interface BlockModelGeneratorsMixin {

	@Accessor("ROTATION_HORIZONTAL_FACING")
	PropertyDispatch<@NotNull VariantMutator> island$getROTATION_HORIZONTAL_FACING();
}
