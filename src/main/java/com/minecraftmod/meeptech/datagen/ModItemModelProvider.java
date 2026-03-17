package com.minecraftmod.meeptech.datagen;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.OreStoneType;
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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, MeepTech.MODID, fileHelper);
    }
    @Override
    protected void registerModels() {
        basicItem(ModItems.MANUAL.get());
        for (Material material : ModMaterials.MATERIALS) {
            for (MaterialForm form : material.getGeneratedForms()) {
                if (form == MaterialForm.HULL) continue;
                if (form == MaterialForm.ORE) continue;
                Item item = material.getForm(form);
                String itemName = BuiltInRegistries.ITEM.getKey(item).getPath();
                withExistingParent(itemName, mcLoc("item/generated"))
                    .texture("layer0", ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "item/material/" + material.getFormTexture(form)));
            }
        }
        for (String modulePath : ModuleItems.MODULE_ITEMS.keySet()) {
            withExistingParent(ModuleItems.MODULE_ITEMS.get(modulePath).getId().getPath(), mcLoc("item/generated"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "item/module/" + modulePath));
        }
        for (DeferredBlock<Block> hullBlock : ModBlocks.HULL_BLOCKS.values()) {
            getBuilder(hullBlock.getId().getPath())
                .parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity")).transforms()
                .transform(ItemDisplayContext.GUI)
                    .rotation(30, 225, 0).translation(0, 0, 0).scale(0.625f, 0.625f, 0.625f).end()
                .transform(ItemDisplayContext.GROUND)
                    .rotation(0, 0, 0).translation(0, 3, 0).scale(0.25f, 0.25f, 0.25f).end()
                .transform(ItemDisplayContext.FIXED)
                    .rotation(0, 0, 0).translation(0, 0, 0).scale(0.5f, 0.5f, 0.5f).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)
                    .rotation(75, 45, 0).translation(0, 2.5f, 0).scale(0.375f, 0.375f, 0.375f).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND)
                    .rotation(0, 45, 0).translation(0, 0, 0).scale(0.4f, 0.4f, 0.4f).end()
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                    .rotation(0, 225, 0).translation(0, 0, 0).scale(0.4f, 0.4f, 0.4f).end()
            .end();
        }
        for (DeferredBlock<Block> oreBlock : ModBlocks.ORE_BLOCKS.values()) {
            String id = oreBlock.getId().getPath();
            ItemModelBuilder builder = getBuilder(id).parent(new UncheckedModelFile(modLoc("block/" + id + "_stone")));
            for (OreStoneType stoneType : OreStoneType.values()) {
                if (stoneType == OreStoneType.STONE) continue;
                builder.override().predicate(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "stone_type"), stoneType.ordinal())
                    .model(new UncheckedModelFile(modLoc("block/" + id + "_" + stoneType.getSerializedName()))).end();
            }
        }
        withExistingParent(ModItems.HAMMER.getId().getPath(), mcLoc("item/generated"))
            .texture("layer0", ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "item/handle_hammer"))
            .texture("layer1", ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "item/base_hammer"));
    }
}
