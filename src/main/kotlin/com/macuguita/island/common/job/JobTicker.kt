/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.job

import com.macuguita.island.common.api.Job
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import java.util.*

object JobTicker {

    private val activeJobs: MutableMap<UUID, Job> = LinkedHashMap()
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
        activeJobs.values.toList().forEach { job ->
            job.tickServer(serverLevel)
        }
    }

    private fun tickClient() {
        activeJobs.values.toList().forEach { job ->
            job.tickClient()
        }
    }

    fun getJob(player: Player): Job? = activeJobs[player.uuid]

    fun getJob(uuid: UUID): Job? = activeJobs[uuid]

    fun startJob(player: ServerPlayer, job: Job) {
        val currentJob = activeJobs[player.uuid]

        if (currentJob === job) {
            return
        }

        currentJob?.end(player)

        activeJobs[player.uuid] = job
        job.start(player)
    }

    fun requestEndJob(player: ServerPlayer) {
        jobsToEnd.add(player.uuid)
    }

    fun requestEndJob(uuid: UUID) {
        jobsToEnd.add(uuid)
    }

    private fun flushEndedJobs(serverLevel: ServerLevel) {
        jobsToEnd.forEach { uuid ->
            val player = serverLevel.getPlayerByUUID(uuid) ?: return@forEach
            activeJobs.remove(uuid)?.end(player as? ServerPlayer ?: return@forEach)
        }
        jobsToEnd.clear()
    }

    fun forEachActiveJob(action: (UUID, Job) -> Unit) {
        activeJobs.toMap().forEach(action)
    }

    fun activeJobsContains(predicate: (UUID, Job) -> Boolean): Boolean =
        activeJobs.any { (uuid, job) -> predicate(uuid, job) }

    fun activeJobsContains(player: Player): Boolean =
        activeJobs.containsKey(player.uuid)

}

