/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server

import com.macuguita.island.common.CommonEntrypoint
import com.macuguita.island.common.attachments.JoinedServer
import com.macuguita.island.server.admin.ConnectionManager
import com.macuguita.island.server.commands.CommandRegistrator
import com.macuguita.island.server.config.ConfigManager
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

object ServerEntrypoint : DedicatedServerModInitializer {

    override fun onInitializeServer() {
        ConfigManager.init(FabricLoader.getInstance().configDir.resolve("${CommonEntrypoint.MOD_ID}.json"))
        ServerWorldEvents.LOAD.register { server, _ -> ConnectionManager.init(server) }
        ServerPlayConnectionEvents.JOIN.register { handler, _, server ->
            val player = handler.player
            val attachedData = JoinedServer.get(player)
            if (!attachedData.joinedServer) {
                server.playerList.broadcastSystemMessage(
                    Component.literal(player.name.string + " has joined for the first time, say hi!")
                        .withStyle(ChatFormatting.YELLOW), false
                )
                attachedData.markJoined()
            }
        }
        CommandRegistrationCallback.EVENT.register(CommandRegistrator.RegisterCommands())
    }
}
