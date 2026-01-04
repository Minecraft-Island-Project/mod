/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.commands

enum class CommandResult(val value: Int) {
    SUCCESS(1),
    ERROR(0);

    companion object {
        fun fromBoolean(success: Boolean): CommandResult =
            if (success) SUCCESS else ERROR
    }
}
