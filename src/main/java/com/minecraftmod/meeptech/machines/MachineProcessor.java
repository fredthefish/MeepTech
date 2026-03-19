package com.minecraftmod.meeptech.machines;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.logic.machine.HeatSource;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.module.ModModuleData;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipe;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;
import com.minecraftmod.meeptech.logic.recipe.ModMachineRecipes;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.logic.ui.UIModuleType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;

public class MachineProcessor {
    public static void serverTick(Level level, BaseMachineBlockEntity entity) {
        MachineData data = entity.getMachineData();
        if (data == null) return;
        MachineProcessing machineProcessing = new MachineProcessing(level, entity, data);
        machineProcessing.runEnergy();
        machineProcessing.startMachines();
        machineProcessing.progress();
        machineProcessing.recipeEnd();
        if (machineProcessing.updated) entity.setChanged();
    }
    static class MachineProcessing {
        public MachineData data;
        public Level level;
        public BaseMachineBlockEntity entity;
        public boolean updated = false;
        public boolean hasEnergy = false;
        public MachineProcessing(Level level, BaseMachineBlockEntity entity, MachineData data) {
            this.level = level;
            this.entity = entity;
            this.data = data;
        }

        public void runEnergy() {
            boolean thisUpdated = false;
            if (data.getEnergySource() instanceof HeatSource heatSource) {
                int heat = entity.getMachineInt(TrackedStat.HeatLeft);
                int fuelSlot = data.getStartSlot(UIModuleType.Energy);
                if (heat > 0) {
                    heat--;
                    updated = true;
                    thisUpdated = true;
                } else if (heat == 0) {
                    MachineRecipeType heatType = heatSource.getHeatType();
                    if (heatType == ModMachineRecipes.SOLID_FUEL) {
                        ItemStack fuelStack = entity.getInventory().getStackInSlot(fuelSlot);
                        if (!fuelStack.isEmpty()) {
                            MachineRecipe recipe = heatType.getRecipe(List.of(fuelStack), List.of());
                            double rate = data.getMachineSpeed();
                            if (recipe != null) {
                                entity.getInventory().extractItem(fuelSlot, 1, false);
                                heat += (int)((double)recipe.getHeat() / rate);
                                updated = true;
                                thisUpdated = true;
                            } else if (fuelStack.getBurnTime(RecipeType.SMELTING) > 0) {
                                heat += (int)((double)fuelStack.getBurnTime(RecipeType.SMELTING) / rate);
                                entity.getInventory().extractItem(fuelSlot, 1, false);
                                updated = true;
                                thisUpdated = true;
                            }
                        }
                    }
                }
                if (heat > 0) hasEnergy = true;
                if (thisUpdated) entity.setMachineInt(TrackedStat.HeatLeft, heat);
            }
        }
        public void startMachines() {
            boolean thisUpdated = false;
            MachineRecipeType recipeType = data.getType().getRecipeType();
            if (recipeType != null) {
                int maxProgress = entity.getMachineInt(TrackedStat.RecipeMaxProgress);
                if (hasEnergy && maxProgress == 0) {
                    List<ItemStack> inputs = new ArrayList<>();
                    List<ItemStack> outputs = new ArrayList<>();
                    for (int i = data.getStartSlot(UIModuleType.Input); i < data.getStartSlot(UIModuleType.Output); i++) 
                        inputs.add(entity.getInventory().getStackInSlot(i).copy());
                    for (int i = data.getStartSlot(UIModuleType.Output); i < data.getStartSlot(UIModuleType.Energy); i++) 
                        outputs.add(entity.getInventory().getStackInSlot(i).copy());
                    MachineRecipe recipe = recipeType.getRecipe(inputs, outputs);
                    if (recipe != null) {
                        int[] consumed = recipe.inputsForConsumption(inputs);
                        for (int i = 0; i < consumed.length; i++) entity.getInventory().extractItem(i, consumed[i], false);
                        entity.setCurrentRecipe(recipe);
                        maxProgress = (int)((double)recipe.getTime() / data.getMachineSpeed());
                        updated = true;
                        thisUpdated = true;
                    } else if (recipeType == ModMachineRecipes.SMELTER) {
                        SingleRecipeInput recipeInput = new SingleRecipeInput(entity.getInventory().getStackInSlot(data.getStartSlot(UIModuleType.Input)));
                        boolean otherRecipe = false;
                        if (data.containsUpgrade(ModModuleData.UPGRADE_BLASTING)) {
                            Optional<RecipeHolder<BlastingRecipe>> blastingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, recipeInput, level);
                            if (blastingRecipe.isPresent()) {
                                BlastingRecipe recipeValue = blastingRecipe.get().value();
                                ItemStack output = entity.getInventory().getStackInSlot(data.getStartSlot(UIModuleType.Output)).copy();
                                ItemStack recipeOutput = recipeValue.getResultItem(level.registryAccess()).copy();
                                if (output.isEmpty() 
                                || (output.getItem().equals(recipeOutput.getItem()) && output.getCount() <= output.getMaxStackSize() + recipeOutput.getCount())) {
                                    entity.getInventory().extractItem(data.getStartSlot(UIModuleType.Input), 1, false);
                                    entity.setCurrentVanillaRecipe(blastingRecipe.get());
                                    maxProgress = (int)((double)recipeValue.getCookingTime() / data.getMachineSpeed());
                                    updated = true;
                                    thisUpdated = true;
                                    otherRecipe = true;
                                }
                            }
                        }
                        if (!otherRecipe) {
                            Optional<RecipeHolder<SmeltingRecipe>> furnaceRecipe = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, recipeInput, level);
                            if (furnaceRecipe.isPresent()) {
                                SmeltingRecipe recipeValue = furnaceRecipe.get().value();
                                ItemStack output = entity.getInventory().getStackInSlot(data.getStartSlot(UIModuleType.Output)).copy();
                                ItemStack recipeOutput = recipeValue.getResultItem(level.registryAccess()).copy();
                                if (output.isEmpty() 
                                || (output.getItem().equals(recipeOutput.getItem()) && output.getCount() <= output.getMaxStackSize() + recipeOutput.getCount())) {
                                    entity.getInventory().extractItem(data.getStartSlot(UIModuleType.Input), 1, false);
                                    entity.setCurrentVanillaRecipe(furnaceRecipe.get());
                                    maxProgress = (int)((double)recipeValue.getCookingTime() / data.getMachineSpeed());
                                    updated = true;
                                    thisUpdated = true;
                                }
                            }
                        }
                    }
                }
                if (thisUpdated) entity.setMachineInt(TrackedStat.RecipeMaxProgress, maxProgress);
            }
        }
        public void progress() {
            int maxProgress = entity.getMachineInt(TrackedStat.RecipeMaxProgress);
            int progress = entity.getMachineInt(TrackedStat.RecipeProgress);
            if (maxProgress > 0) {
                if (hasEnergy) {
                    progress++;
                    updated = true;
                } else if (progress > 0) {
                    progress -= 10;
                    if (progress < 0) progress = 0;
                    updated = true;
                }
                entity.setMachineInt(TrackedStat.RecipeProgress, progress);
            }
        }
        public void recipeEnd() {
            int progress = entity.getMachineInt(TrackedStat.RecipeProgress);
            int maxProgress = entity.getMachineInt(TrackedStat.RecipeMaxProgress);
            int outputSlot = data.getStartSlot(UIModuleType.Output);
            if (progress >= maxProgress && maxProgress > 0) {
                MachineRecipe recipe = entity.getCurrentRecipe();
                if (recipe != null) {
                    ItemStack recipeOutput = recipe.getOutputItems().getFirst().copy();
                    entity.getInventory().insertItem(outputSlot, recipeOutput, false);
                    progress = 0;
                    maxProgress = 0;
                    entity.setCurrentRecipe(null);
                    updated = true;
                } else {
                    String vanillaRecipeId = entity.getCurrentVanillaRecipe();
                    Optional<RecipeHolder<?>> vanillaRecipeHolder = level.getRecipeManager().byKey(ResourceLocation.parse(vanillaRecipeId));
                    if (vanillaRecipeHolder.isPresent()) {
                        Recipe<?> vanillaRecipe = vanillaRecipeHolder.get().value();
                        ItemStack recipeOutput = vanillaRecipe.getResultItem(level.registryAccess()).copy();
                        entity.getInventory().insertItem(outputSlot, recipeOutput, false);
                        progress = 0;
                        maxProgress = 0;
                        entity.setCurrentVanillaRecipe(null);
                        updated = true;
                    }
                }
                entity.setMachineInt(TrackedStat.RecipeProgress, progress);
                entity.setMachineInt(TrackedStat.RecipeMaxProgress, maxProgress);
            }
        }
    }
}
