/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server

import folk.sisby.kaleido.api.WrappedConfig
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment

class ServerConfig : WrappedConfig() {

    @Comment("The message that shows up when a player first joins the server")
    val greetingMessage: String = "%s has joined for the first time, say hi!"

    @Comment("Configurations for the minecraft chat -> discord server connection")
    val discord = Discord()
    class Discord : Section {
        @Comment("Default webhook name")
        val serverName: String = "Island"

        @Comment("Webhook URL")
        val discordWebhookUrl: String = "PLACE_YOUR_WEBHOOK"

        @Comment("Head api url, the %s should be the slot for a player uuid")
        val headApiUrl: String = "https://mc-heads.net/avatar/%s"

        @Comment("Url to image to show for default announcements")
        val defaultImage: String = "https://avatars.githubusercontent.com/u/251938043"
    }
}
