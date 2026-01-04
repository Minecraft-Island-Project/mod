/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.mixin.secret_spectator;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.macuguita.island.server.admin.SecretSpectator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
@Mixin(PlayerList.class)
public class PlayerListMixin {

	@WrapOperation(
			method = "broadcastAll*",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
			)
	)
	private void island$filterBroadcastPlayerInfo(
			ServerGamePacketListenerImpl connection,
			Packet<?> packet,
			Operation<Void> original
	) {
		if (packet instanceof ClientboundPlayerInfoUpdatePacket info) {
			ServerPlayer receiver = connection.player;
			packet = SecretSpectator.INSTANCE.filterPacketForReceiver(receiver, info);
		}
		original.call(connection, packet);
	}

	@WrapOperation(
			method = "placeNewPlayer",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"
			)
	)
	private void island$filterJoinPlayerInfo(
			ServerGamePacketListenerImpl connection,
			Packet<?> packet,
			Operation<Void> original
	) {
		if (packet instanceof ClientboundPlayerInfoUpdatePacket info) {
			ServerPlayer receiver = connection.player;
			packet = SecretSpectator.INSTANCE.filterPacketForReceiver(receiver, info);
		}

		original.call(connection, packet);
	}
}
