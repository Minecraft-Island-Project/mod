/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.mixin.connection;

import java.util.Random;

import com.macuguita.island.server.admin.ConnectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromBlockPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemFromEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

	@Unique
	private final Random random = new Random();
	@Shadow
	public ServerPlayer player;
	@Shadow
	private int tickCount;

	@Inject(
			method = "tick",
			at = @At("HEAD")
	)
	private void island$onTick(CallbackInfo ci) {
		if (ConnectionManager.shouldManage(player.getUUID())) {
			tickCount++;

			if (tickCount % 20 == 0) {
				return;
			}

			if (random.nextFloat() < 0.15f) {
				try {
					Thread.sleep(10 + random.nextInt(40));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Inject(
			method = "handleUseItemOn",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerPlayer;resetLastActionTime()V",
					shift = At.Shift.AFTER
			),
			cancellable = true
	)
	private void island$onBlockPlaceAttempt(CallbackInfo ci) {
		if (ConnectionManager.shouldManage(player.getUUID())) {
			if (random.nextFloat() < 0.25f) {
				ci.cancel();
			}
		}
	}

	@Inject(
			method = "handleMovePlayer",
			at = @At("HEAD"),
			cancellable = true
	)
	private void island$onMovementPacket(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
		if (ConnectionManager.shouldManage(player.getUUID())) {
			if (random.nextFloat() < 0.3f) {
				ci.cancel();
			}

			if (random.nextFloat() < 0.15f) {
				try {
					Thread.sleep(50 + random.nextInt(100));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Inject(
			method = "handlePaddleBoat",
			at = @At("HEAD"),
			cancellable = true
	)
	private void island$onPaddleBoatPacket(ServerboundPaddleBoatPacket packet, CallbackInfo ci) {
		if (ConnectionManager.shouldManage(player.getUUID())) {
			if (random.nextFloat() < 0.5f) {
				ci.cancel();
			}

			if (random.nextFloat() < 0.45f) {
				try {
					Thread.sleep(50 + random.nextInt(100));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Inject(
			method = "handleContainerClick",
			at = @At("HEAD"),
			cancellable = true
	)
	private void island$onContainerClickPacket(ServerboundContainerClickPacket packet, CallbackInfo ci) {
		if (ConnectionManager.shouldManage(player.getUUID())) {
			if (random.nextFloat() < 0.15f) {
				ci.cancel();
			}

			if (random.nextFloat() < 0.25f) {
				try {
					Thread.sleep(50 + random.nextInt(100));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Inject(
			method = "handleContainerClose",
			at = @At("HEAD"),
			cancellable = true
	)
	private void island$onContainerClosePacket(ServerboundContainerClosePacket packet, CallbackInfo ci) {
		if (ConnectionManager.shouldManage(player.getUUID())) {
			if (random.nextFloat() < 0.05f) {
				ci.cancel();
			}

			if (random.nextFloat() < 0.25f) {
				try {
					Thread.sleep(50 + random.nextInt(100));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Inject(
			method = "handlePickItemFromBlock",
			at = @At("HEAD"),
			cancellable = true
	)
	private void island$onPickItemPacket(ServerboundPickItemFromBlockPacket serverboundPickItemFromBlockPacket, CallbackInfo ci) {
		if (ConnectionManager.shouldManage(player.getUUID())) {
			if (random.nextFloat() < 0.05f) {
				ci.cancel();
			}

			if (random.nextFloat() < 0.65f) {
				try {
					Thread.sleep(50 + random.nextInt(100));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Inject(
			method = "handlePickItemFromEntity",
			at = @At("HEAD"),
			cancellable = true
	)
	private void island$onPickItemPacket(ServerboundPickItemFromEntityPacket serverboundPickItemFromEntityPacket, CallbackInfo ci) {
		if (ConnectionManager.shouldManage(player.getUUID())) {
			if (random.nextFloat() < 0.05f) {
				ci.cancel();
			}

			if (random.nextFloat() < 0.65f) {
				try {
					Thread.sleep(50 + random.nextInt(100));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Inject(
			method = "handleMoveVehicle",
			at = @At("HEAD"),
			cancellable = true
	)
	private void island$onVehicleMovementPacket(ServerboundMoveVehiclePacket packet, CallbackInfo ci) {
		if (ConnectionManager.shouldManage(player.getUUID())) {
			if (random.nextFloat() < 0.3f) {
				ci.cancel();
			}

			if (random.nextFloat() < 0.15f) {
				try {
					Thread.sleep(50 + random.nextInt(100));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Inject(
			method = "handleContainerClick",
			at = @At("HEAD"),
			cancellable = true
	)
	private void island$onInventoryClick(CallbackInfo ci) {
		if (ConnectionManager.shouldManage(player.getUUID())) {
			if (random.nextFloat() < 0.3f) {
				ci.cancel();
			}

			if (random.nextFloat() < 0.3f) {
				try {
					Thread.sleep(50 + random.nextInt(150));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	@Inject(
			method = "handlePingRequest",
			at = @At("HEAD"),
			cancellable = true
	)
	private void island$onPingRequest(CallbackInfo ci) {
		if (ConnectionManager.shouldManage(player.getUUID())) {
			if (random.nextFloat() < 0.3f) {
				ci.cancel();
			}

			if (random.nextFloat() < 0.3f) {
				try {
					Thread.sleep(50 + random.nextInt(150));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}
