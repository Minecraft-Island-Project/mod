/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.config.entries

import com.macuguita.island.server.config.ModConfig
import com.mojang.brigadier.arguments.DoubleArgumentType

class DoubleEntry(
    key: String,
    description: String,
    range: ClosedFloatingPointRange<Double>,
    getter: (ModConfig) -> Double,
    setter: (ModConfig, Double) -> ModConfig
) : ConfigEntry<Double>(
    key,
    description,
    getter,
    setter,
    DoubleArgumentType.doubleArg(range.start, range.endInclusive)
)
