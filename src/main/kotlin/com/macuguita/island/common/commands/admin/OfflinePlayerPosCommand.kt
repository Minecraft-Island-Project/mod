/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.commands.admin

import com.macuguita.island.common.commands.CommandRegistrator
import com.macuguita.island.common.commands.CommandResult
import com.macuguita.island.mixin.data.PlayerListAccessor
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import kotlin.jvm.optionals.getOrNull

object OfflinePlayerPosCommand : CommandRegistrator {

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("offlineplayerpos").requires(hasPermission(LEVEL_GAMEMASTERS)).then(
                argument("player", GameProfileArgument.gameProfile()).executes { ctx ->
                    val player = GameProfileArgument.getGameProfiles(ctx, "player").singleOrNull()
                        ?: return@executes CommandResult.ERROR.value
                    val server = ctx.source.server

                    server.playerList.getPlayer(player.id)?.let {
                        ctx.source.sendFailure(Component.literal("The player has to be offline"))
                        return@executes CommandResult.ERROR.value
                    }

                    val handler = (server.playerList as PlayerListAccessor).`island$getPlayerIo`()
                    val nbt = handler.`island$getNbt`(player.id)

                    val list = nbt.getList("Pos")

                    val x = list.getOrNull()?.getDouble(0)?.getOrNull() ?: run {
                        return@executes CommandResult.ERROR.value
                    }
                    val y = list.getOrNull()?.getDouble(0)?.getOrNull() ?: run {
                        return@executes CommandResult.ERROR.value
                    }
                    val z = list.getOrNull()?.getDouble(0)?.getOrNull() ?: run {
                        return@executes CommandResult.ERROR.value
                    }

                    val blockPos = BlockPos(x.toInt(), y.toInt(), z.toInt())

                    val text = Component.literal("${player.name} was last seen at ").append(
                        Component.literal("[${blockPos.x}, ${blockPos.y}, ${blockPos.z}]").withStyle {
                            it.withClickEvent(
                                ClickEvent.RunCommand(
                                    "/tp ${blockPos.x} ${blockPos.y} ${blockPos.z}"
                                )
                            ).withColor(ChatFormatting.GREEN).withHoverEvent(
                                HoverEvent.ShowText(
                                    Component.literal("Click to be teleported")
                                )
                            )
                        }
                    )

                    ctx.source.sendSuccess({ text }, false)

                    return@executes CommandResult.SUCCESS.value
                })

        )
    }
}
