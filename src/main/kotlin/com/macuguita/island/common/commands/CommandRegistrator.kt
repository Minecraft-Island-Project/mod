/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.commands

import com.macuguita.island.common.commands.admin.OfflinePlayerPosCommand
import com.macuguita.island.common.commands.admin.OfflineTpCommand
import com.macuguita.island.common.commands.connection_manager.ConnectionManagerCommand
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

interface CommandRegistrator {

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>)

    class RegisterCommands : CommandRegistrationCallback {
        override fun register(
            dispatcher: CommandDispatcher<CommandSourceStack>,
            buildContext: CommandBuildContext,
            selection: Commands.CommandSelection
        ) {
            OfflineTpCommand.register(dispatcher)
            OfflinePlayerPosCommand.register(dispatcher)
            ConnectionManagerCommand.register(dispatcher)
        }
    }
}
