/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.commands.connection_manager

import com.macuguita.island.common.commands.CommandRegistrator
import com.macuguita.island.common.commands.CommandResult
import com.macuguita.island.server.admin.ConnectionManager
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.network.chat.Component

object ConnectionManagerCommand : CommandRegistrator {

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("connectionmanager")
                .requires(hasPermission(LEVEL_GAMEMASTERS))
                .then(
                    literal("add")
                        .then(
                            argument("player", GameProfileArgument.gameProfile())
                                .executes { ctx ->
                                    val profiles = GameProfileArgument.getGameProfiles(ctx, "player")
                                    var addedCount = 0
                                    val alreadyAdded = mutableListOf<String>()

                                    for (profile in profiles) {
                                        val uuid = profile.id
                                        if (ConnectionManager.shouldManage(uuid)) {
                                            alreadyAdded.add(profile.name)
                                        } else if (ConnectionManager.add(uuid)) {
                                            addedCount++
                                        }
                                    }

                                    val message = buildString {
                                        if (addedCount > 0) append("Added $addedCount player(s) to the connection list.\n")
                                        if (alreadyAdded.isNotEmpty()) append(
                                            "Already present: ${
                                                alreadyAdded.joinToString(
                                                    ", "
                                                )
                                            }"
                                        )
                                    }

                                    ctx.source.sendSuccess({ Component.literal(message.trim()) }, false)
                                    CommandResult.SUCCESS.value
                                }
                        )
                )
                .then(
                    literal("remove")
                        .then(
                            argument("player", GameProfileArgument.gameProfile())
                                .executes { ctx ->
                                    val profiles = GameProfileArgument.getGameProfiles(ctx, "player")
                                    var removedCount = 0
                                    val notPresent = mutableListOf<String>()

                                    for (profile in profiles) {
                                        val uuid = profile.id
                                        if (ConnectionManager.shouldManage(uuid)) {
                                            ConnectionManager.remove(uuid)
                                            removedCount++
                                        } else {
                                            notPresent.add(profile.name)
                                        }
                                    }

                                    val message = buildString {
                                        if (removedCount > 0) append("Removed $removedCount player(s) from the connection list.\n")
                                        if (notPresent.isNotEmpty()) append("Not present: ${notPresent.joinToString(", ")}")
                                    }

                                    ctx.source.sendSuccess({ Component.literal(message.trim()) }, false)
                                    CommandResult.SUCCESS.value
                                }
                        )
                )
        )
    }
}

