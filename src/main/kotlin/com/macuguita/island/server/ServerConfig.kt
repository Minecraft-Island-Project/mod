/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.server

import folk.sisby.kaleido.api.WrappedConfig
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment

class ServerConfig : WrappedConfig() {

    @Comment("The message that shows up when a player first joins the server")
    val greetingMessage: String = "%s has joined for the first time, say hi!"
}
