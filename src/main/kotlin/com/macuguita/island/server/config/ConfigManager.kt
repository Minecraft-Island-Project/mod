/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.config

import com.macuguita.island.server.config.entries.ConfigEntry
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object ConfigManager {

    private lateinit var path: Path

    var config: ModConfig = ModConfig()
        private set

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun init(path: Path) {
        this.path = path
        load()
    }

    fun load() {
        config = if (path.exists()) {
            json.decodeFromString(path.readText())
        } else {
            ModConfig().also { save() }
        }
    }

    fun save() {
        path.parent?.toFile()?.mkdirs()
        path.writeText(json.encodeToString(ModConfig.serializer(), config))
    }

    fun <T : Any> update(entry: ConfigEntry<T>, value: T) {
        config = entry.setter(config, value)
        save()
    }
}
