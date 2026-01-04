/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.admin

import com.macuguita.island.mixin.secret_spectator.ClientboundPlayerInfoUpdatePacketAccessor
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.permissions.Permissions
import net.minecraft.world.level.GameType

@Suppress("KotlinConstantConditions")
object SecretSpectator {

    fun canSeeOtherSpectators(player: ServerPlayer): Boolean =
        player.isSpectator || player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER)

    fun copyPacketWithModifiedEntries(
        packet: ClientboundPlayerInfoUpdatePacket,
        mapper: (ClientboundPlayerInfoUpdatePacket.Entry) -> ClientboundPlayerInfoUpdatePacket.Entry
    ): ClientboundPlayerInfoUpdatePacket {
        val newPacket = ClientboundPlayerInfoUpdatePacket(packet.actions(), emptyList())
        (newPacket as ClientboundPlayerInfoUpdatePacketAccessor).`island$setEntries`(
            packet.entries().map { mapper(it) })
        return newPacket
    }

    fun cloneEntryWithGamemode(
        entry: ClientboundPlayerInfoUpdatePacket.Entry,
        newGamemode: GameType
    ): ClientboundPlayerInfoUpdatePacket.Entry =
        ClientboundPlayerInfoUpdatePacket.Entry(
            entry.profileId,
            entry.profile,
            entry.listed,
            entry.latency,
            newGamemode,
            entry.displayName,
            entry.showHat,
            entry.listOrder,
            entry.chatSession
        )

    fun filterPacketForReceiver(
        receiver: ServerPlayer,
        packet: ClientboundPlayerInfoUpdatePacket
    ): ClientboundPlayerInfoUpdatePacket {
        if (canSeeOtherSpectators(receiver)) return packet

        return copyPacketWithModifiedEntries(packet) { entry ->
            val targetIsSpectator =
                entry.gameMode == GameType.SPECTATOR

            if (targetIsSpectator) {
                cloneEntryWithGamemode(entry, GameType.SURVIVAL)
            } else entry
        }
    }
}
