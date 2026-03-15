package com.minecraftmod.meeptech.items;

import com.minecraftmod.meeptech.ModBlocks;
import com.minecraftmod.meeptech.ModItems;
import com.minecraftmod.meeptech.ModMaterials;
import com.minecraftmod.meeptech.ModModuleData;
import com.minecraftmod.meeptech.ModModuleTypes;
import com.minecraftmod.meeptech.blocks.BaseMachineBlock;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.module.ModuleType;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class MaterialItems {
    public static void registerMaterialItems() {
        for (Material material : ModMaterials.MATERIALS) {
            for (MaterialForm form : material.getGeneratedForms()) {
                String generatedId = material.getId() + "_" + form.getId();
                DeferredItem<Item> generatedItem;
                if (form != ModMaterials.HULL) {
                    generatedItem = ModItems.ITEMS.register(generatedId, () -> new Item(new Item.Properties()));
                } else {
                    DeferredBlock<Block> hullBlock = ModBlocks.registerBlock(generatedId, () -> new BaseMachineBlock(BlockBehaviour.Properties.of()
                        .strength(2f, 6f)
                        .destroyTime(3)
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.STONE)), false);
                    ModBlocks.HULL_BLOCKS.add(hullBlock);
                    generatedItem = ModBlocks.registerBlockHullItem(generatedId, hullBlock);
                    ModuleType hullModule = ModModuleTypes.addModuleType(new ModuleType(generatedId, ModModuleTypes.SLOT_MACHINE_BASE, ModModuleData.BASE_BASIC));
                    hullModule.addSubSlot("machine_core", ModModuleTypes.SLOT_MACHINE_CORE);
                    hullModule.setAssociatedItem(generatedItem);
                }
                material.setForm(form, generatedItem);
            }
        }
    }
}