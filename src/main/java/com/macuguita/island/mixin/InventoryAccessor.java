package com.macuguita.island.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.player.Inventory;

@Mixin(Inventory.class)
public interface InventoryAccessor {

	@Accessor("equipment")
	EntityEquipment island$getEquipment();
}
