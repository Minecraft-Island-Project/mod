/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.commands.config

import com.macuguita.island.server.commands.CommandRegistrator
import com.macuguita.island.server.config.ConfigManager
import com.macuguita.island.server.config.entries.ConfigEntries
import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*
import net.minecraft.network.chat.Component

object ConfigCommand : CommandRegistrator {

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = literal("island")
            .requires(hasPermission(LEVEL_GAMEMASTERS))

        val config = literal("config")
        val set = literal("set")
        val get = literal("get")
        val list = literal("list")

        ConfigEntries.ALL.forEach { entry ->

            set.then(
                literal(entry.key).then(
                    argument("value", entry.argument).executes { ctx ->
                        val value = ctx.getArgument("value", Any::class.java) as Any
                        @Suppress("UNCHECKED_CAST") ConfigManager.update(
                            entry as com.macuguita.island.server.config.entries.ConfigEntry<Any>, value
                        )

                        ctx.source.sendSuccess(
                            { Component.literal("${entry.key} set to $value") }, true
                        )
                        1
                    })
            )

            get.then(
                literal(entry.key).executes {
                    val value = entry.getter(ConfigManager.config)
                    it.source.sendSuccess(
                        { Component.literal("${entry.key} = $value") }, false
                    )
                    1
                })
        }

        list.executes { ctx ->
            ConfigEntries.ALL.forEach {
                val value = it.getter(ConfigManager.config)
                ctx.source.sendSuccess(
                    { Component.literal("${it.key} = $value") }, false
                )
            }
            1
        }

        dispatcher.register(
            root.then(config.then(set)).then(config.then(get)).then(config.then(list))
        )
    }
}
