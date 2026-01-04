/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.mixin.data;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;

import com.macuguita.island.common.Island;
import com.macuguita.island.injected_interfaces.CustomPlayerDataStorage;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.DataResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.PlayerDataStorage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
@Mixin(PlayerDataStorage.class)
public class PlayerDataStorageMixin implements CustomPlayerDataStorage {

	@Shadow
	@Final
	protected DataFixer fixerUpper;
	@Shadow
	@Final
	private File playerDir;

	@Override
	public DataResult<CompoundTag> island$edit(UUID uuid, Consumer<CompoundTag> editor) {
		CompoundTag tag;

		try {
			File file = new File(this.playerDir, uuid.toString() + ".dat");
			if (file.exists() && file.isFile()) {
				tag = NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap());
			} else {
				return DataResult.error(() -> "Player data file for " + uuid + " does not exist");
			}
		} catch (Exception var4) {
			return DataResult.error(() -> "Failed to load player data for " + uuid);
		}

		int i = NbtUtils.getDataVersion(tag, -1);
		tag = DataFixTypes.PLAYER.updateToCurrentVersion(this.fixerUpper, tag, i);

		editor.accept(tag);
		try {
			Path path = this.playerDir.toPath();
			Path path2 = Files.createTempFile(path, uuid + "-", ".dat");
			NbtIo.writeCompressed(tag, path2);
			Path path3 = path.resolve(uuid + ".dat");
			Path path4 = path.resolve(uuid + ".dat_old");
			Util.safeReplaceFile(path3, path2, path4);
		} catch (Exception var6) {
			return DataResult.error(() -> "Failed to save player data for " + uuid);
		}
		return DataResult.success(tag);
	}

	@Override
	public CompoundTag island$getNbt(UUID uuid) {
		CompoundTag tag;

		try {
			File file = new File(this.playerDir, uuid.toString() + ".dat");
			if (file.exists() && file.isFile()) {
				tag = NbtIo.readCompressed(file.toPath(), NbtAccounter.unlimitedHeap());
			} else {
				Island.INSTANCE.getLOGGER().error("Player data file for {} does not exist", uuid);
				return null;
			}
		} catch (Exception var4) {
			Island.INSTANCE.getLOGGER().error("Failed to load player data for {}", uuid);
			return null;
		}

		int i = NbtUtils.getDataVersion(tag, -1);
		return DataFixTypes.PLAYER.updateToCurrentVersion(this.fixerUpper, tag, i);
	}
}
