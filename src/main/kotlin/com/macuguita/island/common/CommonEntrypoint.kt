/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.common

import com.macuguita.island.common.reg.IslandCreativeModeTabs
import com.macuguita.island.common.reg.IslandObjects
import net.fabricmc.api.ModInitializer
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object CommonEntrypoint : ModInitializer {

    const val MOD_ID: String = "island"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    fun id(name: String): Identifier {
        return Identifier.fromNamespaceAndPath(MOD_ID, name)
    }

    override fun onInitialize() {
        IslandObjects.init()
        IslandCreativeModeTabs.init()
    }
}
