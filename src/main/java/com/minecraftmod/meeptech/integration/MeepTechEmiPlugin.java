package com.minecraftmod.meeptech.integration;

import java.util.Map;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.items.ModuleItems;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.MaterialWorkstationRecipe;
import com.minecraftmod.meeptech.logic.material.MaterialWorkstationRecipes;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.logic.module.ModModuleTypes;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipe;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;
import com.minecraftmod.meeptech.logic.recipe.ModMachineRecipes;
import com.minecraftmod.meeptech.registries.ModBlocks;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

@EmiEntrypoint
public class MeepTechEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        for (MachineRecipeType recipeType : ModMachineRecipes.getRecipeTypes().values()) {
            String typeId = recipeType.getId();
            ResourceLocation categoryId = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, typeId);
            Item iconItem = recipeType.getIcon();
            EmiStack icon = iconItem != Items.AIR ? EmiStack.of(iconItem) : EmiStack.of(Items.BARRIER);
            EmiRecipeCategory category = new EmiRecipeCategory(categoryId, icon);
            registry.addCategory(category);
            registry.addWorkstation(category, icon);
            for (MachineRecipe recipe : recipeType.getRecipes()) {
                ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "/" + typeId + "/" + recipe.getId());
                registry.addRecipe(recipe.getEmiRecipe(recipeId, category));
            }
        }
        ResourceLocation workstationId = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "material_workstation");
        EmiStack workstationIcon = EmiStack.of(ModBlocks.MATERIAL_WORKSTATION.get());
        EmiRecipeCategory workstationCategory = new EmiRecipeCategory(workstationId, workstationIcon);
        registry.addCategory(workstationCategory);
        registry.addWorkstation(workstationCategory, workstationIcon);
        for (Material material : ModMaterials.MATERIALS) {
            Map<MaterialForm, ItemLike> forms = material.getForms();
            for (MaterialForm form : forms.keySet()) {
                MaterialWorkstationRecipes recipes = 
                    MaterialWorkstationRecipes.getAvailableForms(new ItemStack(forms.get(form), material.getForm(form).getDefaultMaxStackSize()));
                if (recipes != null) {
                    for (MaterialWorkstationRecipe recipe : recipes.getRecipes()) {
                        ResourceLocation recipeId = 
                            ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, 
                            "/material_workstation/" + material.getId() + "/" + form.getId() + "/" + recipe.getOutputForm().getId());
                        WorkstationEmiRecipe emiRecipe = new WorkstationEmiRecipe(recipeId, workstationCategory, material, recipe);
                        registry.addRecipe(emiRecipe);
                    }
                }
            }
        }
        registry.addWorkstation(VanillaEmiRecipeCategories.SMELTING, 
            EmiIngredient.of(Ingredient.of(ModuleItems.MODULES.get(ModModuleTypes.MACHINE_CORE_SMELTER.getId()))));
    }
}