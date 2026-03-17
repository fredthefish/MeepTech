package com.minecraftmod.meeptech.datagen;

import java.util.concurrent.CompletableFuture;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.ModMaterials;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }
    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        for (Material material : ModMaterials.MATERIALS) {
            if (material.hasGeneratedForm(MaterialForm.ORE)) {
                Item oreItem = material.getForm(MaterialForm.ORE);
                ItemStack outputItem = new ItemStack(material.getForm(MaterialForm.BASE), material.getOreMultiplier());
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(oreItem), RecipeCategory.MISC, outputItem, 0.5f, 200)
                    .unlockedBy("has_ore", has(oreItem))
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, material.getId() + "_ore_smelting"));
                SimpleCookingRecipeBuilder.blasting(Ingredient.of(oreItem), RecipeCategory.MISC, outputItem, 0.5f, 100)
                    .unlockedBy("has_ore", has(oreItem))
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, material.getId() + "_ore_blasting"));
            }
            if (material.hasGeneratedForm(MaterialForm.RAW)) {
                Item rawItem = material.getForm(MaterialForm.RAW);
                ItemStack outputItem = new ItemStack(material.getForm(MaterialForm.BASE), material.getOreMultiplier()); 
                SimpleCookingRecipeBuilder.smelting(Ingredient.of(rawItem), RecipeCategory.MISC, outputItem, 0.5f, 200)
                    .unlockedBy("has_raw_ore", has(rawItem))
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, material.getId() + "_raw_smelting"));
                SimpleCookingRecipeBuilder.blasting(Ingredient.of(rawItem), RecipeCategory.MISC, outputItem, 0.5f, 100)
                    .unlockedBy("has_raw_ore", has(rawItem))
                    .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, material.getId() + "_raw_blasting"));
            }
        }
    }
}
