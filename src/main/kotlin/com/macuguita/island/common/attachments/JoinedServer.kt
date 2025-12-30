/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

@file:Suppress("UnstableApiUsage")

package com.macuguita.island.common.attachments

import com.macuguita.island.common.CommonEntrypoint.id
import com.mojang.serialization.Codec
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget
import net.fabricmc.fabric.api.attachment.v1.AttachmentType

object JoinedServer {

    val ATTACHMENT: AttachmentType<JoinedServerAttachedData> =
        AttachmentRegistry.create(
            id("starter_items")
        ) { builder ->
            builder
                .initializer { JoinedServerAttachedData.DEFAULT }
                .persistent(JoinedServerAttachedData.CODEC)
                .copyOnDeath()
        }

    fun get(target: AttachmentTarget): JoinedServerData =
        JoinedServerData(target)
}

data class JoinedServerAttachedData(
    val joinedServer: Boolean
) {
    companion object {
        val DEFAULT = JoinedServerAttachedData(false)

        val CODEC: Codec<JoinedServerAttachedData> =
            Codec.BOOL.xmap(::JoinedServerAttachedData, JoinedServerAttachedData::joinedServer)
    }

    fun markJoined(): JoinedServerAttachedData =
        copy(joinedServer = true)
}

data class JoinedServerData(private val target: AttachmentTarget) {

    private fun current(): JoinedServerAttachedData =
        target.getAttachedOrElse(JoinedServer.ATTACHMENT, JoinedServerAttachedData.DEFAULT)

    var joinedServer: Boolean
        get() = current().joinedServer
        set(value) {
            target.setAttached(JoinedServer.ATTACHMENT, current().copy(joinedServer = value))
        }

    fun markJoined() {
        target.setAttached(JoinedServer.ATTACHMENT, current().markJoined())
    }
}
