/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server

import com.macuguita.island.common.attachments.JoinedServer
import com.macuguita.island.server.admin.ConnectionManager
import folk.sisby.kaleido.api.WrappedConfig
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
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
        val webhookManager = DiscordWebhookManager()

        ServerPlayConnectionEvents.JOIN.register { handler, _, _ ->
            webhookManager.announce("${handler.player.name.string} joined the server")
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
            webhookManager.announce("${handler.player.name.string} left the server")
        }

        ServerMessageEvents.CHAT_MESSAGE.register { chat, sender, _ ->
            webhookManager.announce(chat.signedContent().trim(), sender)
        }

        ServerLifecycleEvents.SERVER_STARTING.register {
            webhookManager.announce("Server starting...")
        }
        ServerLifecycleEvents.SERVER_STARTED.register {
            webhookManager.announce("Server started.")
        }
        ServerLifecycleEvents.SERVER_STOPPING.register {
            webhookManager.announce("Server stopping...")
        }
        ServerLifecycleEvents.SERVER_STOPPED.register {
            webhookManager.announce("Server stopped.")
        }
    }
}
