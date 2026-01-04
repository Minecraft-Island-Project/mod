/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common.api

import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel

abstract class Job(val id: Identifier) {

    abstract fun tickCommon()
    abstract fun tickClient()
    abstract fun tickServer(serverLevel: ServerLevel)
    abstract fun startJob()
    abstract fun endJob()
}
