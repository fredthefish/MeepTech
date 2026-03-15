package com.minecraftmod.meeptech.datagen;

import java.util.concurrent.CompletableFuture;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.registries.ModItems;
import com.minecraftmod.meeptech.registries.ModTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, 
    CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper fileHelper) {
        super(output, lookupProvider, blockTags, MeepTech.MODID, fileHelper);
    }
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (Material material : ModMaterials.MATERIALS) {
            for (MaterialForm materialForm : material.getForms().keySet()) {
                TagKey<Item> materialFormTag = ModTags.FORM_TAGS.get(materialForm);
                tag(materialFormTag).add(material.getForm(materialForm));
            }
        }
        tag(ModTags.HAMMER_TAG).add(ModItems.HAMMER.get());
    }
}
