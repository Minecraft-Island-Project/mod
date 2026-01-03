package com.macuguita.island.datagen

import com.macuguita.island.common.CommonEntrypoint
import com.macuguita.island.common.reg.IslandObjects
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.*
import java.util.concurrent.CompletableFuture

class IslandLanguageProvider(
    dataOutput: FabricDataOutput,
    registryLookup: CompletableFuture<HolderLookup.Provider>
) : FabricLanguageProvider(dataOutput, "en_us", registryLookup) {

    override fun generateTranslations(
        holderLookup: HolderLookup.Provider,
        translationBuilder: TranslationBuilder
    ) {
        IslandObjects.BLOCKS.entries.forEach {
            generateBlockTranslations(translationBuilder, it.get())
        }
        IslandObjects.ITEMS.entries.forEach {
            generateItemTranslations(translationBuilder, it.get())
        }
        translationBuilder.add("creative_tab.${CommonEntrypoint.MOD_ID}.island", "Furniture")
    }

    private fun capitalizeString(string: String): String {
        val chars = string.lowercase(Locale.getDefault()).toCharArray()
        var found = false
        for (i in chars.indices) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = chars[i].uppercaseChar()
                found = true
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
                found = false
            }
        }
        return String(chars)
    }

    private fun generateBlockTranslations(translationBuilder: TranslationBuilder, block: Block) {
        val temp = capitalizeString(BuiltInRegistries.BLOCK.getKey(block).path.replace("_", " "))
        translationBuilder.add(block, temp)
    }

    private fun generateItemTranslations(translationBuilder: TranslationBuilder, item: Item) {
        val temp = capitalizeString(BuiltInRegistries.ITEM.getKey(item).path.replace("_", " "))
        translationBuilder.add(item, temp)
    }
}
