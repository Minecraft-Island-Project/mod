/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.commands.job

import com.macuguita.island.common.commands.CommandRegistrator
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.*

object JobCommand : CommandRegistrator {

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            literal("jobs").requires(hasPermission(LEVEL_GAMEMASTERS))
                .then(
                    argument("job", StringArgumentType.string())
                        .then(argument("startend", StringArgumentType.string()))
                )
        )
    }
}
