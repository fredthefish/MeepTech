package com.minecraftmod.meeptech.registries;

import java.util.HashMap;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.logic.material.MaterialForm;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static final TagKey<Item> INGOT_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "ingots"));
    public static final TagKey<Item> PLATE_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "plates"));
    public static final TagKey<Item> HULL_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "hulls"));
    public static final TagKey<Item> BOX_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "boxes"));
    public static final TagKey<Item> ORE_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "ores"));
    public static final TagKey<Item> RAW_ORE_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "raw_ores"));

    public static final TagKey<Item> HAMMER_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "hammers"));

    public static final HashMap<MaterialForm, TagKey<Item>> FORM_TAGS = new HashMap<>() {{
        put(MaterialForm.INGOT, INGOT_TAG);
        put(MaterialForm.PLATE, PLATE_TAG);
        put(MaterialForm.HULL, HULL_TAG);
        put(MaterialForm.BOX, BOX_TAG);
        put(MaterialForm.ORE, ORE_TAG);
        put(MaterialForm.RAW, RAW_ORE_TAG);
    }};
}
