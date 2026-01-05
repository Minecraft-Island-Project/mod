/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.api

import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import java.util.UUID

abstract class Job(val id: Identifier) {

    private val activePlayers = mutableSetOf<UUID>()

    fun start(player: ServerPlayer) {
        if (activePlayers.contains(player.uuid)) {
            return
        }
        activePlayers.add(player.uuid)
        startJob(player)
    }

    fun end(player: ServerPlayer) {
        if (!activePlayers.contains(player.uuid)) {
            return
        }
        activePlayers.remove(player.uuid)
        endJob(player)
    }

    abstract fun tickClient()
    abstract fun tickServer(serverLevel: ServerLevel)
    abstract fun startJob(player: ServerPlayer)
    abstract fun endJob(player: ServerPlayer)
}
