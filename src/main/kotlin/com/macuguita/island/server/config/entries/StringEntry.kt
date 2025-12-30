/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.config.entries

import com.macuguita.island.server.config.ModConfig
import com.mojang.brigadier.arguments.StringArgumentType

class StringEntry(
    key: String,
    description: String,
    getter: (ModConfig) -> String,
    setter: (ModConfig, String) -> ModConfig
) : ConfigEntry<String>(
    key,
    description,
    getter,
    setter,
    StringArgumentType.string()
)