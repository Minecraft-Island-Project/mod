/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server

import com.macuguita.island.common.Island
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.minecraft.server.level.ServerPlayer
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class DiscordWebhookManager(
    val discordConfig: ServerConfig.Discord = ServerEntrypoint.CONFIG.discord,
) {

    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build()

    fun announce(
        message: String,
        player: ServerPlayer? = null
    ) {
        val avatarUrl = if (player != null) String.format(discordConfig.headApiUrl, player.uuid) else discordConfig.defaultImage
        val username = player?.name?.string ?: discordConfig.serverName

        val payload = Webhook(
            username = username,
            content = message,
            avatarUrl = avatarUrl,
            allowedMentions = Webhook.AllowedMentions.NONE
        )

        val request = HttpRequest.newBuilder()
            .uri(URI.create(discordConfig.discordWebhookUrl))
            .timeout(Duration.ofSeconds(5))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(Json.encodeToString(payload)))
            .build()

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
            .thenAccept { }
            .exceptionally {
                Island.LOGGER.error("Failed to send discord webhook", it)
                null
            }
    }

    @Serializable
    private data class Webhook(
        val username: String,
        val content: String,
        @SerialName("avatar_url")
        val avatarUrl: String,
        @SerialName("allowed_mentions")
        val allowedMentions: AllowedMentions
    ) {
        @Serializable
        data class AllowedMentions(val parse: List<String>) {
            companion object {
                val NONE = AllowedMentions(emptyList())
            }
        }
    }
}
