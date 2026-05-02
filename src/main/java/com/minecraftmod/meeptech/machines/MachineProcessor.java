package com.minecraftmod.meeptech.machines;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.logic.machine.EnergySource;
import com.minecraftmod.meeptech.logic.machine.EnergySourceType;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.module.ModModuleData;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipe;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipeType;
import com.minecraftmod.meeptech.logic.recipe.ModMachineRecipes;
import com.minecraftmod.meeptech.logic.recipe.MachineRecipe.ConsumptionPlan;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.minecraftmod.meeptech.logic.ui.UIModuleType;
import com.minecraftmod.meeptech.registries.ModFluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

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
            EnergySource energySource = data.getEnergySource();
            if (energySource.getEnergySourceType() == EnergySourceType.Heat) {
                int heat = entity.getMachineInt(TrackedStat.HeatLeft);
                int fuelSlot = data.getStartItemSlot(UIModuleType.Energy);
                if (heat == 0) {
                    MachineRecipeType heatType = energySource.getEnergyType();
                    if (heatType == ModMachineRecipes.SOLID_FUEL) {
                        ItemStack fuelStack = entity.getInventory().getStackInSlot(fuelSlot);
                        if (!fuelStack.isEmpty()) {
                            MachineRecipe recipe = heatType.getRecipe(List.of(fuelStack), List.of(), List.of(), List.of());
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
            } else if (energySource.getEnergySourceType() == EnergySourceType.Steam) {
                int steamSlot = data.getStartFluidSlot(UIModuleType.Energy);
                FluidStack steamStack = entity.getTank(steamSlot).getFluid();
                if (steamStack.getFluid() == ModFluids.STEAM.get() && steamStack.getAmount() > 0) hasEnergy = true;
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
                    List<FluidStack> inputFluids = new ArrayList<>();
                    List<FluidTank> inputTanks = new ArrayList<>();
                    List<FluidTank> outputTanks = new ArrayList<>();
                    for (int i = data.getStartItemSlot(UIModuleType.Input); i < data.getStartItemSlot(UIModuleType.Output); i++) 
                        inputs.add(entity.getInventory().getStackInSlot(i));
                    for (int i = data.getStartItemSlot(UIModuleType.Output); i < data.getStartItemSlot(UIModuleType.Energy); i++) 
                        outputs.add(entity.getInventory().getStackInSlot(i));
                    for (int i = data.getStartFluidSlot(UIModuleType.Input); i < data.getStartFluidSlot(UIModuleType.Output); i++) {
                        inputTanks.add(entity.getTank(i));
                        inputFluids.add(entity.getTank(i).getFluid());
                    }
                    for (int i = data.getStartFluidSlot(UIModuleType.Output); i < data.getStartFluidSlot(UIModuleType.Energy); i++)
                        outputTanks.add(entity.getTank(i));
                    MachineRecipe recipe = recipeType.getRecipe(inputs, outputs, inputFluids, outputTanks);
                    if (recipe != null) {
                        ConsumptionPlan consumptionPlan = recipe.inputsForConsumption(inputs, inputFluids);
                        consumptionPlan.execute(inputs, inputTanks);
                        entity.setCurrentRecipe(recipe);
                        maxProgress = (int)((double)recipe.getTime() / data.getMachineSpeed());
                        updated = true;
                        thisUpdated = true;
                    } else if (recipeType == ModMachineRecipes.SMELTER) {
                        SingleRecipeInput recipeInput = new SingleRecipeInput(entity.getInventory().getStackInSlot(data.getStartItemSlot(UIModuleType.Input)));
                        boolean otherRecipe = false;
                        if (data.containsUpgrade(ModModuleData.UPGRADE_BLASTING)) {
                            Optional<RecipeHolder<BlastingRecipe>> blastingRecipe = level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, recipeInput, level);
                            if (blastingRecipe.isPresent()) {
                                BlastingRecipe recipeValue = blastingRecipe.get().value();
                                ItemStack output = entity.getInventory().getStackInSlot(data.getStartItemSlot(UIModuleType.Output)).copy();
                                ItemStack recipeOutput = recipeValue.getResultItem(level.registryAccess()).copy();
                                if (output.isEmpty() 
                                || (output.getItem().equals(recipeOutput.getItem()) && output.getCount() <= output.getMaxStackSize() + recipeOutput.getCount())) {
                                    entity.getInventory().extractItem(data.getStartItemSlot(UIModuleType.Input), 1, false);
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
                                ItemStack output = entity.getInventory().getStackInSlot(data.getStartItemSlot(UIModuleType.Output)).copy();
                                ItemStack recipeOutput = recipeValue.getResultItem(level.registryAccess()).copy();
                                if (output.isEmpty() 
                                || (output.getItem().equals(recipeOutput.getItem()) && output.getCount() <= output.getMaxStackSize() + recipeOutput.getCount())) {
                                    entity.getInventory().extractItem(data.getStartItemSlot(UIModuleType.Input), 1, false);
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
                    EnergySourceType energySourceType = data.getEnergySource().getEnergySourceType();
                    if (energySourceType == EnergySourceType.Heat) {
                        int heat = entity.getMachineInt(TrackedStat.HeatLeft);
                        if (heat > 0) {
                            heat--;
                            entity.setMachineInt(TrackedStat.HeatLeft, heat);
                        }
                    } else if (energySourceType == EnergySourceType.Steam) {
                        int steamSlot = data.getStartFluidSlot(UIModuleType.Energy);
                        entity.getTank(steamSlot).drain(1, FluidAction.EXECUTE);
                    }
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
            int outputSlot = data.getStartItemSlot(UIModuleType.Output);
            if (progress >= maxProgress && maxProgress > 0) {
                MachineRecipe recipe = entity.getCurrentRecipe();
                if (recipe != null) {
                    List<ItemStack> recipeOutputs = recipe.getOutputItems();
                    List<FluidStack> recipeOutputFluids = recipe.getOutputFluids();
                    for (ItemStack output : recipeOutputs) {
                        int remaining = output.getCount();
                        for (int i = data.getStartItemSlot(UIModuleType.Output); i < data.getStartItemSlot(UIModuleType.Energy) && remaining > 0; i++) {
                            ItemStack slot = entity.getInventory().getStackInSlot(i);
                            if (slot.isEmpty()) continue;
                            if (!ItemStack.isSameItemSameComponents(slot, output)) continue;
                            int space = Math.min(64, slot.getMaxStackSize()) - slot.getCount();
                            if (space <= 0) continue;
                            int place = Math.min(remaining, space);
                            slot.grow(place);
                            remaining -= place;
                        }
                        for (int i = data.getStartItemSlot(UIModuleType.Output); i < data.getStartItemSlot(UIModuleType.Energy) && remaining > 0; i++) {
                            ItemStack slot = entity.getInventory().getStackInSlot(i);
                            if (!slot.isEmpty()) continue;
                            int place = Math.min(remaining, Math.min(64, output.getMaxStackSize()));
                            entity.getInventory().setStackInSlot(i, output.copyWithCount(place));
                            remaining -= place;
                        }
                    }
                    for (FluidStack output : recipeOutputFluids) {
                        int remaining = output.getAmount();
                        for (int i = data.getStartFluidSlot(UIModuleType.Output); i < data.getStartFluidSlot(UIModuleType.Energy) && remaining > 0; i++) {
                            FluidTank tank = entity.getTank(i);
                            FluidStack inTank = tank.getFluid();
                            if (inTank.isEmpty()) continue;
                            if (!FluidStack.isSameFluidSameComponents(inTank, output)) continue;
                            int space = tank.getCapacity() - inTank.getAmount();
                            if (space <= 0) continue;
                            int place = Math.min(remaining, space);
                            inTank.grow(place);
                            remaining -= place;
                        }
                        for (int i = data.getStartFluidSlot(UIModuleType.Output); i < data.getStartFluidSlot(UIModuleType.Energy) && remaining > 0; i++) {
                            FluidTank tank = entity.getTank(i);
                            if (!tank.getFluid().isEmpty()) continue;
                            int place = Math.min(remaining, tank.getCapacity());
                            tank.setFluid(output.copyWithAmount(place));
                            remaining -= place;
                        }
                    }
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
