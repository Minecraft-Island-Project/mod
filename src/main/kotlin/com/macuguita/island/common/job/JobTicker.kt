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
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.*

object JobTicker {

    private val jobStartQueue: MutableList<Pair<UUID, Identifier>> = Collections.synchronizedList(mutableListOf())
    private val jobEndQueue: MutableSet<UUID> = Collections.synchronizedSet(mutableSetOf())

    fun init() {
        ServerTickEvents.END_SERVER_TICK.register { server ->
            val serverLevel = server.getLevel(Level.OVERWORLD) ?: return@register
            flushQueuedChanges(serverLevel)
            tickServer(serverLevel)
        }

        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) {
            ClientTickEvents.END_CLIENT_TICK.register { _ ->
                tickClient()
            }
        }
    }

    private fun flushQueuedChanges(serverLevel: ServerLevel) {
        val activeJobs = ActiveJobsSavedData.getActiveJobs(serverLevel.server)

        jobEndQueue.toList().forEach { uuid ->
            val player = serverLevel.getPlayerByUUID(uuid) ?: return@forEach
            val jobId = activeJobs[uuid] ?: return@forEach

            if (activeJobs.remove(uuid)) {
                IslandJobs.JOBS[jobId]?.endJob(player as ServerPlayer)
            }
        }
        jobEndQueue.clear()

        jobStartQueue.toList().forEach { (uuid, newJobId) ->
            val player = serverLevel.getPlayerByUUID(uuid) as? ServerPlayer ?: return@forEach
            val currentJobId = activeJobs[uuid]

            if (currentJobId == newJobId) return@forEach

            currentJobId?.let { prevJobId ->
                IslandJobs.JOBS[prevJobId]?.endJob(player)
            }

            val hadActiveJob = activeJobs[uuid] != null
            activeJobs[uuid] = newJobId
            IslandJobs.JOBS[newJobId]?.startJob(player, hadActiveJob)
        }
        jobStartQueue.clear()
    }

    private fun tickServer(serverLevel: ServerLevel) {
        val activeJobs = ActiveJobsSavedData.getActiveJobs(serverLevel.server)
        activeJobs.allValues()
            .distinct()
            .forEach { jobId ->
                IslandJobs.JOBS[jobId]?.tickServer(serverLevel)
            }
    }

    private fun tickClient() {
        //TODO: handle differently because active jobs is server only
//        activeJobs.allValues()
//            .distinct()
//            .forEach { jobId ->
//                IslandJobs.JOBS[jobId]?.tickClient()
//            }
    }

    fun getJob(player: Player): Job? {
        val server = (player as? ServerPlayer)?.level()?.server ?: return null
        val activeJobs = ActiveJobsSavedData.getActiveJobs(server)
        return IslandJobs.JOBS[activeJobs[player.uuid]]
    }

    fun requestStartJob(player: ServerPlayer, jobId: Identifier) {
        jobStartQueue.add(player.uuid to jobId)
    }

    fun requestEndJob(player: ServerPlayer) {
        jobEndQueue.add(player.uuid)
    }

    fun forEachActiveJob(server: MinecraftServer, action: (UUID, Identifier) -> Unit) {
        val activeJobs = ActiveJobsSavedData.getActiveJobs(server)
        activeJobs.allEntries().forEach(action)
    }
}
