package com.macuguita.island.datagen

import com.macuguita.island.common.reg.IslandObjects
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class IslandBlockLootTableProvider(
    dataOutput: FabricDataOutput,
    registryLookup: CompletableFuture<HolderLookup.Provider>
) : FabricBlockLootTableProvider(dataOutput, registryLookup) {

    override fun generate() {
        IslandObjects.BLOCKS.entries.forEach {
            dropSelf(it.get())
        }
    }
}
