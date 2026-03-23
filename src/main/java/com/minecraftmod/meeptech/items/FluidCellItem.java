package com.minecraftmod.meeptech.items;

import java.util.List;

import com.minecraftmod.meeptech.registries.ModDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;

public class FluidCellItem extends Item {
    private final int capacity;
    public FluidCellItem(int capacity, Properties props) {
        super(props.stacksTo(64));
        this.capacity = capacity;
    }
    public int getCapacity() { 
        return capacity; 
    }
    public FluidStack getFluid(ItemStack stack) {
        SimpleFluidContent content = stack.get(ModDataComponents.FLUID_CELL_CONTENT.get());
        return content != null ? content.copy() : FluidStack.EMPTY;
    }
    public int getFluidAmount(ItemStack stack) {
        return getFluid(stack).getAmount();
    }
    public boolean isEmpty(ItemStack stack) {
        return getFluid(stack).isEmpty();
    }
    public boolean isFull(ItemStack stack) {
        return getFluidAmount(stack) >= capacity;
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        FluidStack fluid = getFluid(stack);
        if (!fluid.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.meeptech.fluid_cell", fluid.getFluid().getFluidType().getDescription(), fluid.getAmount(), capacity));
        } else {
            tooltip.add(Component.translatable("tooltip.meeptech.fluid_cell_empty", capacity));
        }
    }
    @Override
    public Component getName(ItemStack stack) {
        if (isEmpty(stack)) return Component.translatable("item.meeptech.fluid_cell.empty");
        FluidStack fluidStack = getFluid(stack);
        return Component.translatable(fluidStack.getDescriptionId()).append(" ").append(Component.translatable("item.meeptech.fluid_cell"));
    }
}
