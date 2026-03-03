/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.meeptech.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

import net.minecraft.world.item.Item;

import net.mcreator.meeptech.item.HammerItem;
import net.mcreator.meeptech.MeeptechMod;

public class MeeptechModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(MeeptechMod.MODID);
	public static final DeferredItem<Item> HAMMER;
	static {
		HAMMER = REGISTRY.register("hammer", HammerItem::new);
	}
	// Start of user code block custom items
	// End of user code block custom items
}