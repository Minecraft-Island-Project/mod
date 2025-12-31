/*
 * Copyright (c) 2025 macuguita. All Rights Reserved.
 */

package com.macuguita.island.datagen

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object DatagenEntrypoint : DataGeneratorEntrypoint {

    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()

        pack.addProvider(::IslandBlockStateGenerator)
    }
}
