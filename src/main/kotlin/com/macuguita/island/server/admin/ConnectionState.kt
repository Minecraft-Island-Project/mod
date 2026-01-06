/*
 * Copyright (c) 2025-2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.admin

import com.macuguita.island.shim.SavedDataTypes
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import java.util.*

class ConnectionState private constructor(private val uuidsInternal: MutableSet<UUID> = HashSet()) : SavedData() {

    constructor() : this(mutableSetOf())

    fun add(uuid: UUID): Boolean = uuidsInternal.add(uuid).also { if (it) setDirty() }

    fun remove(uuid: UUID): Boolean = uuidsInternal.remove(uuid).also { if (it) setDirty() }

    fun contains(uuid: UUID): Boolean = uuidsInternal.contains(uuid)

    fun all(): Set<UUID> = Collections.unmodifiableSet(HashSet(uuidsInternal))

    fun shouldManage(uuid: UUID) = uuidsInternal.contains(uuid)

    companion object {
        val CODEC: Codec<ConnectionState> = RecordCodecBuilder.create { inst ->
            inst.group(
                UUIDUtil.CODEC_SET.fieldOf("uuids").forGetter { it.uuidsInternal }
            ).apply(inst, ::ConnectionState)
        }

        @JvmField
        val TYPE: SavedDataType<ConnectionState> = SavedDataTypes.create(
            "connection_manager",
            ::ConnectionState,
            CODEC,
            null
        )

        @JvmStatic
        fun getConnectionState(server: MinecraftServer): ConnectionState {
            val level =
                server.getLevel(ServerLevel.OVERWORLD) ?: return ConnectionState()

            return level.dataStorage.computeIfAbsent(TYPE)
        }
    }
}
