/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server

import com.macuguita.island.common.attachments.JoinedServer
import com.macuguita.island.server.admin.ConnectionManager
import folk.sisby.kaleido.api.WrappedConfig
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

object ServerEntrypoint : DedicatedServerModInitializer {

    val CONFIG: ServerConfig =
        WrappedConfig.createToml(FabricLoader.getInstance().configDir, "island", "server", ServerConfig::class.java)

    override fun onInitializeServer() {
        ServerWorldEvents.LOAD.register { server, _ -> ConnectionManager.init(server) }
        ServerPlayConnectionEvents.JOIN.register { handler, _, server ->
            val player = handler.player
            val attachedData = JoinedServer.get(player)
            if (!attachedData.joinedServer) {
                server.playerList.broadcastSystemMessage(
                    Component.literal(String.format(CONFIG.greetingMessage, player.name.string))
                        .withStyle(ChatFormatting.YELLOW), false
                )
                attachedData.markJoined()
            }
        }
    }
}
