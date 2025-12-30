/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.commands.config

import com.macuguita.island.server.commands.CommandRegistrator
import com.macuguita.island.server.config.ConfigManager
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*
import net.minecraft.network.chat.Component

object ReloadConfig : CommandRegistrator {

    override fun register(dispatcher: com.mojang.brigadier.CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("island").requires(hasPermission(LEVEL_GAMEMASTERS)).then(
                literal("config").then(
                    literal("reload").executes {
                        ConfigManager.load()
                        it.source.sendSuccess(
                            { Component.literal("Config reloaded") }, true
                        )
                        1
                    })
            )
        )
    }
}
