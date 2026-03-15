package com.minecraftmod.meeptech.registries;

import net.neoforged.neoforge.registries.DeferredRegister;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.items.GuideItem;
import com.minecraftmod.meeptech.items.HammerItem;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MeepTech.MODID);

    public static final DeferredItem<Item> MANUAL = ITEMS.register("manual", () -> new GuideItem(new Item.Properties()
        .stacksTo(1)));
    public static final DeferredItem<Item> HAMMER = ITEMS.register("stone_hammer", () -> new HammerItem(new Item.Properties()
        .durability(64)));
}
