package com.macuguita.island.datagen

import com.macuguita.island.common.reg.IslandBlockTags
import com.macuguita.island.common.reg.IslandObjects
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class IslandBlockTagProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : BlockTagProvider(output, registriesFuture) {

    override fun addTags(p0: HolderLookup.Provider) {
        IslandObjects.BEAMS.entries.forEach {
            valueLookupBuilder(IslandBlockTags.BEAM)
                .add(it.get())
        }
    }
}
