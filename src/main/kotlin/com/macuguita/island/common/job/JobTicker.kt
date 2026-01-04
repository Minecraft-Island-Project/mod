/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.job

import com.macuguita.island.common.api.Job
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import java.util.*

object JobTicker {

    // Map of player UUID -> current active job
    val activeJobs: MutableMap<UUID, Job> = mutableMapOf()

    fun init() {
        // Server-side tick
        ServerTickEvents.END_WORLD_TICK.register { serverLevel ->
            tickServer(serverLevel)
        }
    }

    private fun tickServer(serverLevel: ServerLevel) {
        activeJobs.forEach { (_, job) ->
            job.tickServer(serverLevel)
        }
    }

    fun getJob(player: Player): Job? = activeJobs[player.uuid]

    fun getJob(uuid: UUID): Job? = activeJobs[uuid]

    fun startJob(player: ServerPlayer, job: Job) {
        activeJobs[player.uuid]?.endJob()

        activeJobs[player.uuid] = job
        job.startJob()
    }

    fun endJob(player: ServerPlayer) {
        activeJobs[player.uuid]?.endJob()
        activeJobs.remove(player.uuid)
    }
}

