package com.minecraftmod.meeptech;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static final TagKey<Item> BASE_MATERIAL_TAG = 
        TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "base_materials"));
    public static final TagKey<Item> PLATE_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "plates"));
    public static final TagKey<Item> HULL_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "hulls"));
}
