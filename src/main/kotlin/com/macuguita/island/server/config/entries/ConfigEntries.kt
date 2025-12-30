/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.config.entries

object ConfigEntries {

    val ALL = listOf(
        StringEntry(
            key = "joinMessage",
            description = "Changes the welcome message of first join",
            getter = { it.joinMessage },
            setter = { c, v -> c.copy(joinMessage = v) }
        ),
    )

    val BY_KEY = ALL.associateBy { it.key }
}
