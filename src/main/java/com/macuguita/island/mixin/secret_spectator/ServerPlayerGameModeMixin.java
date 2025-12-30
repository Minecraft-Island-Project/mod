/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.mixin.secret_spectator;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.macuguita.island.server.admin.SecretSpectator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.players.PlayerList;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

	@Shadow
	@Final
	protected ServerPlayer player;

	@WrapOperation(
			method = "changeGameModeForPlayer",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/players/PlayerList;broadcastAll(Lnet/minecraft/network/protocol/Packet;)V"
			)
	)
	private void island$wrapChangeGameMode(
			PlayerList playerList,
			Packet<?> packet,
			Operation<Void> original
	) {
		if (!(packet instanceof ClientboundPlayerInfoUpdatePacket info)) {
			original.call(playerList, packet);
			return;
		}

		for (ServerPlayer receiver : playerList.getPlayers()) {
			ClientboundPlayerInfoUpdatePacket filtered =
					SecretSpectator.INSTANCE.filterPacketForReceiver(receiver, info);
			receiver.connection.send(filtered);
		}
	}
}
