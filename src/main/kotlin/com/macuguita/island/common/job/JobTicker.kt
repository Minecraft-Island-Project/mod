/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.job

import com.macuguita.island.common.api.Job
import com.macuguita.island.common.reg.IslandJobs
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import java.util.*

object JobTicker {

    private val activeJobs: MutableMap<UUID, Identifier> = Collections.synchronizedMap<UUID, Identifier>(emptyMap()).toMutableMap()
    private val jobsToEnd = mutableSetOf<UUID>()

    fun init() {
        ServerTickEvents.END_WORLD_TICK.register { serverLevel ->
            tickServer(serverLevel)
            flushEndedJobs(serverLevel)
        }
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClientTickEvents.END_CLIENT_TICK.register { _ ->
                tickClient()
            }
        }
    }

    private fun tickServer(serverLevel: ServerLevel) {
        activeJobs.values
            .distinct()
            .forEach { jobId ->
                IslandJobs.JOBS[jobId]?.tickServer(serverLevel)
            }
    }

    private fun tickClient() {
        activeJobs.values
            .distinct()
            .forEach { jobId ->
                IslandJobs.JOBS[jobId]?.tickClient()
            }
    }

    fun getJob(player: Player): Job? = IslandJobs.JOBS[activeJobs[player.uuid]]

    fun getJob(uuid: UUID): Job? = IslandJobs.JOBS[activeJobs[uuid]]

    fun startJob(player: ServerPlayer, jobId: Identifier) {
        val uuid = player.uuid
        val currentJobId = activeJobs[uuid]

        if (currentJobId == jobId) return

        currentJobId?.let { prevJobId ->
            IslandJobs.JOBS[prevJobId]?.end(player)
        }

        activeJobs[uuid] = jobId
        IslandJobs.JOBS[jobId]?.start(player)
    }

    fun requestEndJob(player: ServerPlayer) {
        jobsToEnd.add(player.uuid)
    }

    fun requestEndJob(uuid: UUID) {
        jobsToEnd.add(uuid)
    }

    private fun flushEndedJobs(serverLevel: ServerLevel) {
        jobsToEnd.toList().forEach { uuid ->
            val player = serverLevel.getPlayerByUUID(uuid) ?: return@forEach
            val jobId = activeJobs.remove(uuid) ?: return@forEach
            IslandJobs.JOBS[jobId]?.end(player as ServerPlayer)
        }
        jobsToEnd.clear()
    }

    fun forEachActiveJob(action: (UUID, Identifier) -> Unit) {
        activeJobs.toMap().forEach(action)
    }
}

