package com.minecraftmod.meeptech.emi;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.ModMachineRecipes;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipe;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

@EmiEntrypoint
public class MeepTechEmiPlugin implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        for (MachineRecipeType recipeType : ModMachineRecipes.getRecipeTypes()) {
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
    }
}
