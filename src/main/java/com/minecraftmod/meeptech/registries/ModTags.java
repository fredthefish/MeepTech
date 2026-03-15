package com.minecraftmod.meeptech.registries;

import java.util.HashMap;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.ModMaterials;

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
    public static final TagKey<Item> HAMMER_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "hammers"));

    public static final HashMap<MaterialForm, TagKey<Item>> FORM_TAGS = new HashMap<>() {{
        put(ModMaterials.BASE, BASE_MATERIAL_TAG);
        put(ModMaterials.PLATE, PLATE_TAG);
        put(ModMaterials.HULL, HULL_TAG);
        put(ModMaterials.BOX, BOX_TAG);
    }};
}
