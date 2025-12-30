/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server.admin

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.server.MinecraftServer
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import java.util.*

object ConnectionManager {

    private lateinit var state: ConnectionState

    fun init(server: MinecraftServer) {
        val manager = server.overworld().dataStorage
        @Suppress("UNCHECKED_CAST")
        state = manager.computeIfAbsent(ConnectionState.TYPE)
    }

    fun shouldManage(uuid: UUID): Boolean = state.contains(uuid)

    fun add(uuid: UUID): Boolean = state.add(uuid)

    fun remove(uuid: UUID): Boolean = state.remove(uuid)

    fun all(): Set<UUID> = state.all()
}

class ConnectionState(private val uuidsInternal: MutableSet<UUID> = HashSet()) : SavedData() {

    constructor(list: List<UUID>) : this(HashSet(list))

    fun add(uuid: UUID): Boolean = uuidsInternal.add(uuid).also { if (it) setDirty() }

    fun remove(uuid: UUID): Boolean = uuidsInternal.remove(uuid).also { if (it) setDirty() }

    fun contains(uuid: UUID): Boolean = uuidsInternal.contains(uuid)

    fun all(): Set<UUID> = Collections.unmodifiableSet(HashSet(uuidsInternal))

    companion object {
        const val ID = "connection_manager"

        val CODEC: Codec<ConnectionState> = RecordCodecBuilder.create { inst ->
            inst.group(
                UUIDUtil.CODEC_SET.fieldOf("uuids").forGetter { it.uuidsInternal }
            ).apply(inst, ::ConnectionState)
        }

        @JvmField
        val TYPE: SavedDataType<ConnectionState> = SavedDataType(ID, ::ConnectionState, CODEC, DataFixTypes.LEVEL)
    }
}
