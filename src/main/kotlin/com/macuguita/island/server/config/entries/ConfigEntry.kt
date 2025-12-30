/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.config.entries

import com.macuguita.island.server.config.ModConfig
import com.mojang.brigadier.arguments.ArgumentType

sealed class ConfigEntry<T : Any>(
    val key: String,
    val description: String,
    val getter: (ModConfig) -> T,
    val setter: (ModConfig, T) -> ModConfig,
    val argument: ArgumentType<T>
)