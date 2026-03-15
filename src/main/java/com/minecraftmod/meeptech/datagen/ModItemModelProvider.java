package com.minecraftmod.meeptech.datagen;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.items.ModuleItems;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.registries.ModBlocks;
import com.minecraftmod.meeptech.registries.ModItems;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, MeepTech.MODID, fileHelper);
    }
    @Override
    protected void registerModels() {
        basicItem(ModItems.MANUAL.get());
        withExistingParent(ModBlocks.MATERIAL_WORKSTATION.getId().getPath(), modLoc("block/material_workstation"));
        withExistingParent(ModBlocks.ENGINEERING_STATION.getId().getPath(), modLoc("block/engineering_station"));
        for (Material material : ModMaterials.MATERIALS) {
            for (MaterialForm form : material.getGeneratedForms()) {
                if (form != ModMaterials.HULL) {
                    Item item = material.getForm(form);
                    String itemName = BuiltInRegistries.ITEM.getKey(item).getPath();
                    withExistingParent(itemName, mcLoc("item/generated"))
                        .texture("layer0", ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "item/material/" + material.getFormTexture(form)));
                }
            }
        }
        for (String modulePath : ModuleItems.MODULE_ITEMS.keySet()) {
            withExistingParent(ModuleItems.MODULE_ITEMS.get(modulePath).getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "item/module/" + modulePath));
        }
    }
}
