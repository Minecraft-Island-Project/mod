package com.macuguita.island.client.job

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState

data class JobZoneMasterBlockEntityRenderState(
    var posX: Int = 0,
    var posY: Int = 0,
    var posZ: Int = 0,
    var sizeX: Int = 0,
    var sizeY: Int = 0,
    var sizeZ: Int = 0,
    var shouldRender: Boolean = false,
) : BlockEntityRenderState()
