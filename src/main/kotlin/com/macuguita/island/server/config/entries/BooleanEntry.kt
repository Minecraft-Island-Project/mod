/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.config.entries

import com.macuguita.island.server.config.ModConfig
import com.mojang.brigadier.arguments.BoolArgumentType

class BooleanEntry(
    key: String,
    description: String,
    getter: (ModConfig) -> Boolean,
    setter: (ModConfig, Boolean) -> ModConfig
) : ConfigEntry<Boolean>(
    key,
    description,
    getter,
    setter,
    BoolArgumentType.bool()
)