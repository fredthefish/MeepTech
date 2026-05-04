package com.minecraftmod.meeptech.logic.recipe;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.minecraftmod.meeptech.items.ModuleItems;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.registries.ModFluids;
import com.minecraftmod.meeptech.registries.ModItems;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;

public class ModMachineRecipes {
    private static Map<String, MachineRecipeType> RECIPES = new LinkedHashMap<>();
    private static boolean isInitialized = false;

    public static MachineRecipeType SMELTER = addRecipeType(new MachineRecipeType("smelter", ModuleItems.SMELTER_CORE).setItemIO(1, 1));
    public static MachineRecipeType ALLOYER = addRecipeType(new MachineRecipeType("alloyer", ModuleItems.ALLOYER_CORE).setItemIO(2, 1));
    public static MachineRecipeType SOLID_FUEL = 
        addRecipeType(new MachineRecipeType("solid_fuel", ModuleItems.SOLID_FUEL_CORE).setItemIO(1, 0).setHasHeat(true));
    public static MachineRecipeType BOILER = addRecipeType(new MachineRecipeType("boiler", ModuleItems.STEAM_BOILER_CORE).setFluidIO(1, 1));
    public static MachineRecipeType COKER = addRecipeType(new MachineRecipeType("coker", ModuleItems.COKER_CORE)).setItemIO(1, 1);
    public static MachineRecipeType PRESSER = addRecipeType(new MachineRecipeType("presser", ModuleItems.PRESSER_CORE)).setItemIO(1, 1);
    public static MachineRecipeType WATER_PUMPER = 
        addRecipeType(new MachineRecipeType("water_pumper", ModuleItems.WATER_PUMPER_CORE)).setFluidIO(1, 1);
    public static MachineRecipeType MOLDER = addRecipeType(new MachineRecipeType("molder", ModuleItems.MOLDER_CORE)).setItemIO(2, 1);

    public static void registerRecipes() {
        SOLID_FUEL.addRecipe(new MachineRecipe("burn_sugar_cane", SOLID_FUEL).setInputItems(List.of(SizedIngredient.of(Items.SUGAR_CANE, 1))).setHeat(30));
        SOLID_FUEL.addRecipe(new MachineRecipe("burn_coke", SOLID_FUEL)
            .setInputItems(List.of(SizedIngredient.of(ModMaterials.COKE.getForm(MaterialForm.BASE), 1))).setHeat(320));

        ALLOYER.addRecipe(new MachineRecipe("alloy_bronze", ALLOYER)
            .setInputItems(List.of(SizedIngredient.of(Items.COPPER_INGOT, 3), SizedIngredient.of(ModMaterials.TIN.getForm(MaterialForm.BASE), 1)))
            .setOutputItems(List.of(new ItemStack(ModMaterials.BRONZE.getForm(MaterialForm.BASE), 4))).setTime(200));
        ALLOYER.addRecipe(new MachineRecipe("alloy_pig_iron", ALLOYER)
            .setInputItems(List.of(SizedIngredient.of(Items.IRON_INGOT, 1), SizedIngredient.of(ModMaterials.COKE.getForm(MaterialForm.BASE), 1)))
            .setOutputItems(List.of(new ItemStack(ModMaterials.PIG_IRON.getForm(MaterialForm.BASE)))).setTime(200));

        BOILER.addRecipe(new MachineRecipe("boil_water", BOILER)
            .setInputFluids(List.of(new FluidStack(Fluids.WATER, 80)))
            .setOutputFluids(List.of(new FluidStack(ModFluids.STEAM.get(), 80))).setTime(20));
        
        COKER.addRecipe(new MachineRecipe("coke_coal", COKER).setTime(200)
            .setInputItems(List.of(SizedIngredient.of(Items.COAL, 1)))
            .setOutputItems(List.of(new ItemStack(ModMaterials.COKE.getForm(MaterialForm.BASE)))));

        WATER_PUMPER.addRecipe(new MachineRecipe("pump_water", WATER_PUMPER).setTime(10)
            .setFluidCatalysts(List.of(new FluidStack(Fluids.WATER, 2000)))
            .setOutputFluids(List.of(new FluidStack(Fluids.WATER, 160))));

        for (Material material : ModMaterials.MATERIALS) {
            if (material.hasForm(MaterialForm.BASE) && material.hasForm(MaterialForm.PLATE)) {
                PRESSER.addRecipe(new MachineRecipe("press_" + material.getId(), PRESSER)
                    .setTime(60 + 40 * material.getMaterialTier())
                    .setInputItems(List.of(SizedIngredient.of(material.getForm(MaterialForm.BASE), 1)))
                    .setOutputItems(List.of(new ItemStack(material.getForm(MaterialForm.PLATE)))));
            }
            if (material.hasForm(MaterialForm.PLATE)) {
                if (material.hasForm(MaterialForm.GEAR)) MOLDER.addRecipe(new MachineRecipe("mold_gear_" + material.getId(), MOLDER).setTime(160)
                    .setInputItems(List.of(SizedIngredient.of(material.getForm(MaterialForm.PLATE), 4)))
                    .setItemCatalysts(List.of(SizedIngredient.of(ModItems.MOLD_GEAR, 1)))
                    .setOutputItems(List.of(new ItemStack(material.getForm(MaterialForm.GEAR)))));
                if (material.hasForm(MaterialForm.ROTOR)) MOLDER.addRecipe(new MachineRecipe("mold_rotor_" + material.getId(), MOLDER).setTime(160)
                    .setInputItems(List.of(SizedIngredient.of(material.getForm(MaterialForm.PLATE), 4)))
                    .setItemCatalysts(List.of(SizedIngredient.of(ModItems.MOLD_ROTOR, 1)))
                    .setOutputItems(List.of(new ItemStack(material.getForm(MaterialForm.ROTOR)))));
            }
        }
        for (String mold : ModItems.MOLDS.keySet()) {
            MOLDER.addRecipe(new MachineRecipe("mold_copy_" + mold, MOLDER).setTime(100)
                .setInputItems(List.of(SizedIngredient.of(ModItems.MOLD, 1)))
                .setItemCatalysts(List.of(SizedIngredient.of(ModItems.MOLDS.get(mold), 1)))
                .setOutputItems(List.of(new ItemStack(ModItems.MOLDS.get(mold).get()))));
        }
        
        isInitialized = true;
    }
    private static MachineRecipeType addRecipeType(MachineRecipeType type) {
        RECIPES.put(type.getId(), type);
        return type;
    }
    public static Map<String, MachineRecipeType> getRecipeTypes() {
        if (!isInitialized) registerRecipes();
        return RECIPES;
    }
    public static MachineRecipeType getRecipeType(String id) {
        if (!isInitialized) registerRecipes();
        return RECIPES.get(id);
    }
}
