package com.minecraftmod.meeptech;

import java.util.HashMap;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.ItemStack;

import com.minecraftmod.meeptech.items.ModuleItems;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.MaterialItemData;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MeepTech.MODID);

    public static final Supplier<CreativeModeTab> MATERIALS_TAB = CREATIVE_MODE_TABS.register("meeptech_materials", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup." + MeepTech.MODID + ".materials_tab"))
        .icon(() -> new ItemStack(ModItems.IRON_PLATE.get()))
        .displayItems((params, output) -> {
            for (Material material : ModMaterials.MATERIALS) {
                HashMap<MaterialForm, Item> forms = material.getForms();
                for (Item item : forms.values()) {
                    output.accept(item);
                }
            }
        })
        .build()
    );

    public static final Supplier<CreativeModeTab> MEEPTECH_TAB = CREATIVE_MODE_TABS.register("meeptech_general", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup." + MeepTech.MODID + ".general_tab"))
        .icon(() -> new ItemStack(ModItems.HAMMER.get()))
        .displayItems((params, output) -> {
            output.accept(ModItems.MANUAL.get());
            output.accept(ModItems.HAMMER.get());
            output.accept(ModBlocks.MATERIAL_WORKSTATION.get());
            output.accept(ModBlocks.ENGINEERING_STATION.get());
            ModuleItems.MODULES.values().forEach(item -> {
                //Materials have their own tab.
                if (new MaterialItemData(item.get()).getMaterial() == null) output.accept(item.get());
            });
        })
        .build()
    );
}
