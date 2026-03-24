package com.minecraftmod.meeptech.datagen;

import java.util.concurrent.CompletableFuture;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.items.ModuleItems;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.registries.ModBlocks;
import com.minecraftmod.meeptech.registries.ModItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.HAMMER.get())
            .pattern(" II").pattern("SII").pattern(" II")
            .define('S', Items.STICK).define('I', ItemTags.STONE_TOOL_MATERIALS)
            .unlockedBy("has_stone", has(ItemTags.STONE_TOOL_MATERIALS))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "stone_hammer"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MATERIAL_WORKSTATION.get())
            .pattern("SSS").pattern("SHS").pattern("SSS")
            .define('S', Items.STONE).define('H', ModItems.HAMMER.get())
            .unlockedBy("has_hammer", has(ModItems.HAMMER.get()))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "material_workstation"));
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MATERIAL_WORKSTATION.get())
            .pattern("BMB").pattern("MBM").pattern("BMB")
            .define('B', Items.BRICK).define('M', ModuleItems.TEMPLATE_PRIMITIVE.get())
            .unlockedBy("has_module", has(ModuleItems.TEMPLATE_PRIMITIVE.get()))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "engineering_station"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MANUAL.get())
            .requires(Items.BOOK).requires(ModuleItems.TEMPLATE_PRIMITIVE.get())
            .unlockedBy("has_module", has(ModuleItems.TEMPLATE_PRIMITIVE.get()))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "manual"));
        //Modules
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModuleItems.TEMPLATE_PRIMITIVE)
            .requires(ItemTags.STONE_CRAFTING_MATERIALS).requires(Items.PAPER)
            .unlockedBy("has_paper", has(Items.PAPER))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "modules/template_primitive"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModuleItems.TEMPLATE_STEAM)
            .requires(ModMaterials.BRONZE.getForm(MaterialForm.PLATE)).requires(Items.PAPER)
            .unlockedBy("has_bronze", has(ModMaterials.BRONZE.getForm(MaterialForm.PLATE)))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "modules/template_steam"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModuleItems.SMELTER_CORE)
            .requires(ModuleItems.TEMPLATE_PRIMITIVE).requires(Items.FURNACE)
            .unlockedBy("has_furnace", has(Items.FURNACE))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "modules/machine_core_smelter"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModuleItems.ALLOYER_CORE)
            .requires(ModuleItems.TEMPLATE_PRIMITIVE).requires(Items.FURNACE).requires(ModMaterials.COPPER.getForm(MaterialForm.ROTOR))
            .unlockedBy("has_rotor", has(ModMaterials.COPPER.getForm(MaterialForm.ROTOR)))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "modules/machine_core_alloyer"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModuleItems.SOLID_FUEL_CORE)
            .requires(ModuleItems.TEMPLATE_PRIMITIVE).requires(ItemTags.COALS)
            .unlockedBy("has_coals", has(ItemTags.COALS))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "modules/heating_core_solid_fuel"));
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModuleItems.SMELTER_BLASTING)
            .requires(ModuleItems.TEMPLATE_PRIMITIVE).requires(Items.BLAST_FURNACE)
            .unlockedBy("has_blast_furnace", has(Items.BLAST_FURNACE))
            .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "modules/upgrade_smelter_blasting"));
    }
}
