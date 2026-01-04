/*
 * Copyright (c) 2026 macuguita. All Rights Reserved.
 */

package com.macuguita.island.datagen

import com.macuguita.island.common.reg.IslandItemTags
import com.macuguita.island.common.reg.IslandObjects
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class IslandItemTagProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricTagProvider.ItemTagProvider(output, registriesFuture) {

    override fun addTags(p0: HolderLookup.Provider) {
        valueLookupBuilder(IslandItemTags.SECATEURS)
            .add(IslandObjects.SECATEURS.get())
    }
}
