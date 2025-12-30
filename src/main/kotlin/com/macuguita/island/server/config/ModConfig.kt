/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.config

import kotlinx.serialization.Serializable

// Remember to update ConfigEntries when changing stuff!!!
@Serializable
data class ModConfig(
    @ConfigOption("Changes the welcome message of first join")
    val joinMessage: String = "%s has joined for the first time, say hi!",
)