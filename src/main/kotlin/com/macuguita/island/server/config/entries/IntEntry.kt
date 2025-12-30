/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.config.entries

import com.macuguita.island.server.config.ModConfig
import com.mojang.brigadier.arguments.IntegerArgumentType

class IntEntry(
    key: String,
    description: String,
    range: IntRange,
    getter: (ModConfig) -> Int,
    setter: (ModConfig, Int) -> ModConfig
) : ConfigEntry<Int>(
    key,
    description,
    getter,
    setter,
    IntegerArgumentType.integer(range.first, range.last)
)