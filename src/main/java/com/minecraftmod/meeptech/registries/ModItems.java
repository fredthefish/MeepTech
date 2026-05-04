package com.minecraftmod.meeptech.registries;

import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.pipes.PipeConnection;
import com.minecraftmod.meeptech.blocks.pipes.PipeBlockEntity.PipeType;
import com.minecraftmod.meeptech.items.FluidCellItem;
import com.minecraftmod.meeptech.items.GuideItem;
import com.minecraftmod.meeptech.items.HammerItem;
import com.minecraftmod.meeptech.items.PipeAttachmentItem;
import com.minecraftmod.meeptech.items.WrenchItem;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MeepTech.MODID);
    public static final Map<String, DeferredItem<Item>> MOLDS = new HashMap<>();

    public static final DeferredItem<Item> MANUAL = ITEMS.register("manual", () -> new GuideItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> HAMMER = ITEMS.register("stone_hammer", () -> new HammerItem(new Item.Properties().durability(64)));
    public static final DeferredItem<Item> ITEM_EXTRACTOR = 
        ITEMS.register("item_extractor", () -> new PipeAttachmentItem(PipeConnection.EXTRACTOR, PipeType.ITEM, new Item.Properties()));
    public static final DeferredItem<Item> ITEM_INSERTER = 
        ITEMS.register("item_inserter", () -> new PipeAttachmentItem(PipeConnection.INSERTER, PipeType.ITEM, new Item.Properties()));
    public static final DeferredItem<Item> FLUID_EXTRACTOR = 
        ITEMS.register("fluid_extractor", () -> new PipeAttachmentItem(PipeConnection.EXTRACTOR, PipeType.FLUID, new Item.Properties()));
    public static final DeferredItem<Item> FLUID_INSERTER = 
        ITEMS.register("fluid_inserter", () -> new PipeAttachmentItem(PipeConnection.INSERTER, PipeType.FLUID, new Item.Properties()));
    public static final DeferredItem<Item> WRENCH = ITEMS.register("wrench", () -> new WrenchItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<FluidCellItem> FLUID_CELL = ModItems.ITEMS.register("fluid_cell", 
        () -> new FluidCellItem(1000, new Item.Properties()));
    public static final DeferredItem<Item> MOLD = ITEMS.register("mold", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> MOLD_GEAR = addMoldItem("gear");
    public static final DeferredItem<Item> MOLD_ROTOR = addMoldItem("rotor");

    private static DeferredItem<Item> addMoldItem(String name) {
        DeferredItem<Item> moldItem = ITEMS.register("mold_" + name, () -> new Item(new Item.Properties()));
        MOLDS.put(name, moldItem);
        return moldItem;
    }
}
