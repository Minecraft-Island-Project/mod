/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.injected_interfaces;

import java.util.UUID;
import java.util.function.Consumer;

import com.mojang.serialization.DataResult;

import net.minecraft.nbt.CompoundTag;

public interface CustomPlayerDataStorage {

	DataResult<CompoundTag> island$edit(UUID uuid, Consumer<CompoundTag> editor);

	CompoundTag island$getNbt(UUID uuid);
}
