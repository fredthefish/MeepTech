package com.minecraftmod.meeptech.registries;

import net.neoforged.neoforge.registries.DeferredRegister;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.pipes.PipeConnection;
import com.minecraftmod.meeptech.items.GuideItem;
import com.minecraftmod.meeptech.items.HammerItem;
import com.minecraftmod.meeptech.items.PipeAttachmentItem;
import com.minecraftmod.meeptech.items.WrenchItem;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MeepTech.MODID);

    public static final DeferredItem<Item> MANUAL = ITEMS.register("manual", () -> new GuideItem(new Item.Properties()
        .stacksTo(1)));
    public static final DeferredItem<Item> HAMMER = ITEMS.register("stone_hammer", () -> new HammerItem(new Item.Properties()
        .durability(64)));
    
    public static final DeferredItem<Item> EXTRACTOR = ITEMS.register("extractor", 
        () -> new PipeAttachmentItem(PipeConnection.EXTRACTOR, new Item.Properties()));
    public static final DeferredItem<Item> INSERTER = ITEMS.register("inserter", 
        () -> new PipeAttachmentItem(PipeConnection.INSERTER, new Item.Properties()));
    public static final DeferredItem<Item> WRENCH = ITEMS.register("wrench", () -> new WrenchItem(new Item.Properties()));
}
