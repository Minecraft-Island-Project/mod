package com.macuguita.island.common.util

import net.minecraft.world.level.block.Block

interface WoodType {

    fun getName(): String
    fun getPlank(): Block?
    fun getLog(): Block
    fun isStripped(): Boolean
}
