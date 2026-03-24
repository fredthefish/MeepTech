package com.minecraftmod.meeptech.machines;

import java.util.List;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class CombinedFluidHandler implements IFluidHandler {
    private final List<FluidTank> tanks;
    public CombinedFluidHandler(List<FluidTank> tanks) {
        this.tanks = tanks;
    }
    @Override
    public int getTanks() {
        return tanks.size();
    }
    @Override
    public FluidStack getFluidInTank(int tank) {
        return tanks.get(tank).getFluidInTank(0);
    }
    @Override
    public int getTankCapacity(int tank) {
        return tanks.get(tank).getTankCapacity(0);
    }
    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return tanks.get(tank).isFluidValid(stack);
    }
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled = 0;
        FluidStack remaining = resource.copy();
        for (FluidTank tank : tanks) {
            if (remaining.isEmpty()) break;
            int accepted = tank.fill(remaining, action);
            filled += accepted;
            remaining.shrink(accepted);
        }
        return filled;
    }
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        for (FluidTank tank : tanks) {
            FluidStack drained = tank.drain(resource, action);
            if (!drained.isEmpty()) return drained;
        }
        return FluidStack.EMPTY;
    }
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (FluidTank tank : tanks) {
            FluidStack drained = tank.drain(maxDrain, action);
            if (!drained.isEmpty()) return drained;
        }
        return FluidStack.EMPTY;
    }
}
