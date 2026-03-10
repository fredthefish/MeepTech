package com.minecraftmod.meeptech;

import java.util.HashMap;

import com.minecraftmod.meeptech.logic.MaterialForm;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static final TagKey<Item> BASE_MATERIAL_TAG = 
        TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "base_materials"));
    public static final TagKey<Item> PLATE_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "plates"));
    public static final TagKey<Item> HULL_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "hulls"));
    public static final TagKey<Item> BOX_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "boxes"));

    public static final HashMap<MaterialForm, TagKey<Item>> FORM_TAGS = new HashMap<>() {{
        put(MaterialForm.Base, BASE_MATERIAL_TAG);
        put(MaterialForm.Plate, PLATE_TAG);
        put(MaterialForm.Hull, HULL_TAG);
        put(MaterialForm.Box, BOX_TAG);
    }};
}
