/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.mixin.connection;

import java.util.Random;

import com.macuguita.island.server.admin.ConnectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.SERVER)
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	@Unique
	private final Random random = new Random();
	@Unique
	private Vec3 lastPosition = Vec3.ZERO;
	@Unique
	private int teleportCooldown = 0;

	@Inject(
			method = "checkMovementStatistics",
			at = @At("HEAD")
	)
	private void island$onPositionCheck(double dx, double dy, double dz, CallbackInfo ci) {
		ServerPlayer player = (ServerPlayer) (Object) this;

		if (ConnectionManager.shouldManage(player.getUUID())) {
			Vec3 currentPos = player.position();

			double movementDistance = currentPos.distanceTo(lastPosition);
			lastPosition = currentPos;

			if (teleportCooldown > 0) {
				teleportCooldown--;
			}

			if (movementDistance > 0.01 && teleportCooldown == 0) {
				if (random.nextFloat() < 0.15f) {
					Vec3 velocity = player.getDeltaMovement();
					double speed = velocity.length();

					if (speed > 0.05) {
						double pullback = 0.05 + random.nextDouble() * 0.15;

						Vec3 direction = velocity.normalize();

						double newX = currentPos.x - direction.x * pullback;
						double newY = currentPos.y;
						double newZ = currentPos.z - direction.z * pullback;

						player.connection.teleport(newX, newY, newZ, player.getYRot(), player.getXRot());
						teleportCooldown = 40 + random.nextInt(40);
					}
				}
			}
		}
	}
}
