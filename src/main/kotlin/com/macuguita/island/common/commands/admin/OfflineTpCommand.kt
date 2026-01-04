/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.commands.admin

import com.macuguita.island.common.commands.CommandRegistrator
import com.macuguita.island.common.commands.CommandResult
import com.macuguita.island.mixin.data.PlayerListAccessor
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.commands.arguments.coordinates.BlockPosArgument
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component

object OfflineTpCommand : CommandRegistrator {

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("offlinetp").requires(hasPermission(LEVEL_GAMEMASTERS))
                .then(
                    argument("player", GameProfileArgument.gameProfile()).then(
                        argument("pos", BlockPosArgument.blockPos()).executes { ctx ->
                            val player = GameProfileArgument.getGameProfiles(ctx, "player").singleOrNull()
                                ?: return@executes CommandResult.ERROR.value
                            val pos = BlockPosArgument.getLoadedBlockPos(ctx, "pos")
                            val server = ctx.source.server

                            server.playerList.getPlayer(player.id)?.let {
                                ctx.source.sendFailure(Component.literal("The player has to be offline"))
                                return@executes CommandResult.ERROR.value
                            }

                            val handler = (server.playerList as PlayerListAccessor).`island$getPlayerIo`()
                            handler.`island$edit`(player.id) {
                                it.put(
                                    "Pos", newDoubleList(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
                                )
                                it.putString("Dimension", ctx.source.level.dimension().toString())
                            }

                            ctx.source.sendSuccess(
                                { Component.literal("Teleported ${player.name} to ${pos.x}, ${pos.y}, ${pos.z}") },
                                true
                            )
                            CommandResult.SUCCESS.value
                        })
                )
        )
    }

    fun newDoubleList(vararg numbers: Double): ListTag {
        val nbtList = ListTag()
        for (d in numbers) {
            nbtList.add(DoubleTag.valueOf(d))
        }

        return nbtList
    }
}
