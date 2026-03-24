package com.minecraftmod.meeptech.logic.recipe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.minecraftmod.meeptech.integration.MachineEmiRecipe;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class MachineRecipe {
    private String id;
    private MachineRecipeType type;
    private List<SizedIngredient> inputItems = new ArrayList<>();
    private List<ItemStack> outputItems = new ArrayList<>();
    private List<SizedFluidIngredient> inputFluids = new ArrayList<>();
    private List<FluidStack> outputFluids = new ArrayList<>();
    private int time;
    private int heat;
    public MachineRecipe(String id, MachineRecipeType type) {
        this.id = id;
        this.type = type;
    }
    public MachineRecipe setInputItems(List<SizedIngredient> inputs) {
        this.inputItems.addAll(inputs);
        return this;
    }
    public MachineRecipe setOutputItems(List<ItemStack> outputs) {
        this.outputItems.addAll(outputs);
        return this;
    }
    public MachineRecipe setInputFluids(List<SizedFluidIngredient> inputs) {
        this.inputFluids.addAll(inputs);
        return this;
    }
    public MachineRecipe setOutputFluids(List<FluidStack> outputs) {
        this.outputFluids.addAll(outputs);
        return this;
    }
    public MachineRecipe setTime(int time) {
        this.time = time;
        return this;
    }
    public MachineRecipe setHeat(int heat) {
        this.heat = heat;
        return this;
    }
    public String getId() {
        return id;
    }
    public MachineRecipeType getType() {
        return type;
    }
    public List<ItemStack> getOutputItems() {
        List<ItemStack> output = new ArrayList<>();
        for (ItemStack stack : outputItems) {
            output.add(stack.copy());
        }
        return output;
    }
    public List<FluidStack> getOutputFluids() {
        List<FluidStack> output = new ArrayList<>();
        for (FluidStack stack : outputFluids) {
            output.add(stack.copy());
        }
        return output;
    }
    public int getTime() {
        return time;
    }
    public int getHeat() {
        return heat;
    }
    public boolean fullInputs(List<ItemStack> inputItems, List<FluidStack> inputFluids) {
        int[] claimedItems = new int[inputItems.size()];
        int[] claimedFluids = new int[inputFluids.size()];
        for (SizedIngredient required : this.inputItems) {
            int still = required.count();
            for (int i = 0; i < inputItems.size() && still > 0; i++) {
                ItemStack stack = inputItems.get(i);
                if (stack.isEmpty()) continue;
                if (!required.ingredient().test(stack)) continue;
                int available = stack.getCount() - claimedItems[i];
                if (available <= 0) continue;
                int take = Math.min(available, still);
                claimedItems[i] += take;
                still -= take;
            }
            if (still > 0) return false;
        }
        for (SizedFluidIngredient required : this.inputFluids) {
            int still = required.amount();
            for (int i = 0; i < inputFluids.size() && still > 0; i++) {
                FluidStack stack = inputFluids.get(i);
                if (stack.isEmpty()) continue;
                if (!required.ingredient().test(stack)) continue;
                int available = stack.getAmount() - claimedFluids[i];
                if (available <= 0) continue;
                int take = Math.min(available, still);
                claimedFluids[i] += take;
                still -= take;
            }
            if (still > 0) return false;
        }
        return true;
    }
    public ConsumptionPlan inputsForConsumption(List<ItemStack> inputItems, List<FluidStack> inputFluids) {
        Map<Integer, Integer> itemConsumption = new LinkedHashMap<>();
        Map<Integer, Integer> fluidConsumption = new LinkedHashMap<>();
        int[] claimedItems = new int[inputItems.size()];
        int[] claimedFluids = new int[inputFluids.size()];
        for (SizedIngredient required : this.inputItems) {
            int still = required.count();
            for (int i = 0; i < inputItems.size() && still > 0; i++) {
                ItemStack stack = inputItems.get(i);
                if (stack.isEmpty()) continue;
                if (!required.ingredient().test(stack)) continue;
                int available = stack.getCount() - claimedItems[i];
                if (available <= 0) continue;
                int take = Math.min(available, still);
                claimedItems[i] += take;
                itemConsumption.merge(i, take, Integer::sum);
                still -= take;
            }
        }
        for (SizedFluidIngredient required : this.inputFluids) {
            int still = required.amount();
            for (int i = 0; i < inputFluids.size() && still > 0; i++) {
                FluidStack stack = inputFluids.get(i);
                if (stack.isEmpty()) continue;
                if (!required.ingredient().test(stack)) continue;
                int available = stack.getAmount() - claimedFluids[i];
                if (available <= 0) continue;
                int take = Math.min(available, still);
                claimedFluids[i] += take;
                fluidConsumption.merge(i, take, Integer::sum);
                still -= take;
            }
        }
        return new ConsumptionPlan(itemConsumption, fluidConsumption);
    }
    public boolean canOutput(List<ItemStack> outputSlots, List<FluidTank> outputTanks) {
        List<ItemStack> simSlots = new ArrayList<>();
        for (ItemStack stack : outputSlots) {
            simSlots.add(stack.copy());
        }
        for (ItemStack output : this.outputItems) {
            int remaining = output.getCount();
            for (int i = 0; i < simSlots.size() && remaining > 0; i++) {
                ItemStack slot = simSlots.get(i);
                if (slot.isEmpty()) {
                    int place = Math.min(remaining, 64);
                    simSlots.set(i, output.copyWithCount(remaining));
                    remaining -= place;
                } else if (ItemStack.isSameItemSameComponents(slot, output)) {
                    int space = Math.min(64, slot.getMaxStackSize()) - slot.getCount();
                    if (space <= 0) continue;
                    int place = Math.min(remaining, space);
                    slot.grow(place);
                    remaining -= place;
                }
            }
            if (remaining > 0) return false;
        }
        List<FluidStack> simTanks = new ArrayList<>();
        List<Integer> simCapacities = new ArrayList<>();
        for (FluidTank tank : outputTanks) {
            simTanks.add(tank.getFluid().copy());
            simCapacities.add(tank.getCapacity());
        }
        for (FluidStack output : this.outputFluids) {
            int remaining = output.getAmount();
            for (int i = 0; i < simTanks.size() && remaining > 0; i++) {
                FluidStack tankFluid = simTanks.get(i);
                int capacity = simCapacities.get(i);
                if (tankFluid.isEmpty()) {
                    int place = Math.min(remaining, capacity);
                    simTanks.set(i, output.copyWithAmount(place));
                    remaining -= place;
                } else if (FluidStack.isSameFluidSameComponents(tankFluid, output)) {
                    int space = capacity - tankFluid.getAmount();
                    if (space <= 0) continue;
                    int place = Math.min(remaining, space);
                    tankFluid.grow(place);
                    remaining -= place;
                }
            }
            if (remaining > 0) return false;
        }
        return true;
    }
    public List<EmiIngredient> getEmiInputs() {
        List<EmiIngredient> emiIngredients = new ArrayList<>();
        for (SizedIngredient ingredient : inputItems) emiIngredients.add(EmiIngredient.of(ingredient.ingredient(), ingredient.count()));
        return emiIngredients;
    }
    public List<EmiStack> getEmiOutputs() {
        List<EmiStack> emiStacks = new ArrayList<>();
        for (ItemStack stack : outputItems) emiStacks.add(EmiStack.of(stack));
        return emiStacks;
    }
    public MachineEmiRecipe getEmiRecipe(ResourceLocation syntheticId, EmiRecipeCategory category) {
        return new MachineEmiRecipe(syntheticId, category, this, type);
    }
    public record ConsumptionPlan(Map<Integer, Integer> itemConsumption, Map<Integer, Integer> fluidConsumption) {
        public void execute(List<ItemStack> inputItems, List<FluidTank> inputTanks) {
            for (var entry : itemConsumption.entrySet()) {
                inputItems.get(entry.getKey()).shrink(entry.getValue());
            }
            for (var entry : fluidConsumption.entrySet()) {
                FluidTank tank = inputTanks.get(entry.getKey());
                tank.drain(entry.getValue(), IFluidHandler.FluidAction.EXECUTE);
            }
        }
    }
}
