package com.macuguita.island.shim;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import org.jspecify.annotations.Nullable;

import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class SavedDataTypes {

	public static <T extends SavedData> SavedDataType<T> create(String id, Supplier<T> constructor, Codec<T> codec, @Nullable DataFixTypes dataFixType) {
		return new SavedDataType<>(id, constructor, codec, dataFixType);
	}
}
