package com.minecraftmod.meeptech.registries;

import java.util.HashMap;
import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.fluids.BaseFlowingFluid.Source;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.items.ModuleItems;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.MaterialItemData;
import com.minecraftmod.meeptech.logic.material.ModMaterials;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MeepTech.MODID);

    public static final Supplier<CreativeModeTab> MATERIALS_TAB = CREATIVE_MODE_TABS.register("meeptech_materials", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup." + MeepTech.MODID + ".materials_tab"))
        .icon(() -> new ItemStack(ModMaterials.PIG_IRON.getForm(MaterialForm.BASE)))
        .displayItems((params, output) -> {
            for (Material material : ModMaterials.MATERIALS) {
                HashMap<MaterialForm, ItemLike> forms = material.getForms();
                for (ItemLike item : forms.values()) {
                    output.accept(item.asItem());
                }
            }
        })
        .build());

    public static final Supplier<CreativeModeTab> MEEPTECH_TAB = CREATIVE_MODE_TABS.register("meeptech_general", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup." + MeepTech.MODID + ".general_tab"))
        .icon(() -> new ItemStack(ModItems.HAMMER.get()))
        .displayItems((params, output) -> {
            output.accept(ModItems.MANUAL.get());
            output.accept(ModItems.HAMMER.get());
            output.accept(ModItems.WRENCH.get());
            output.accept(ModBlocks.MATERIAL_WORKSTATION.get());
            output.accept(ModBlocks.ENGINEERING_STATION.get());
            output.accept(ModBlocks.ITEM_PIPE.get());
            output.accept(ModItems.ITEM_EXTRACTOR.get());
            output.accept(ModItems.ITEM_INSERTER.get());
            output.accept(ModBlocks.FLUID_PIPE.get());
            output.accept(ModItems.FLUID_EXTRACTOR.get());
            output.accept(ModItems.FLUID_INSERTER.get());
            output.accept(ModBlocks.FLUID_TANK.get());
            output.accept(ModItems.FLUID_CELL.get());
            output.accept(ModItems.MOLD.get());
            output.accept(ModItems.MOLD_GEAR.get());
            output.accept(ModItems.MOLD_ROTOR.get());
            ModuleItems.MODULES.values().forEach(item -> {
                //Materials have their own tab.
                if (new MaterialItemData(item.get()).getMaterial() == null) output.accept(item.get());
            });
        })
        .build());

    public static final Supplier<CreativeModeTab> FLUID_TAB = CREATIVE_MODE_TABS.register("meeptech_fluids", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.meeptech.fluid_tab"))
        .icon(() -> new ItemStack(ModItems.FLUID_CELL.get()))
        .displayItems((params, output) -> {
            ItemStack waterCell = new ItemStack(ModItems.FLUID_CELL.get());
            waterCell.set(ModDataComponents.FLUID_CELL_CONTENT.get(), SimpleFluidContent.copyOf(new FluidStack(Fluids.WATER, 1000)));
            output.accept(waterCell);
            ItemStack lavaCell = new ItemStack(ModItems.FLUID_CELL.get());
            lavaCell.set(ModDataComponents.FLUID_CELL_CONTENT.get(), SimpleFluidContent.copyOf(new FluidStack(Fluids.LAVA, 1000)));
            output.accept(lavaCell);
            for (DeferredHolder<Fluid, Source> entry : ModFluids.SOURCE_FLUIDS) {
                ItemStack fluidCell = new ItemStack(ModItems.FLUID_CELL.get());
                fluidCell.set(ModDataComponents.FLUID_CELL_CONTENT.get(), SimpleFluidContent.copyOf(new FluidStack(entry.get(), 1000)));
                output.accept(fluidCell);
            }
        })
        .build());
}
