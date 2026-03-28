package com.minecraftmod.meeptech.machines;

import java.util.List;

import com.minecraftmod.meeptech.logic.ui.SlotType;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class CombinedFluidHandler implements IFluidHandler {
    private final List<FluidTankWithData> tanks;
    private final boolean automation;
    public CombinedFluidHandler(List<FluidTankWithData> tanks, boolean automation) {
        this.tanks = tanks;
        this.automation = automation;
    }
    @Override
    public int getTanks() {
        return tanks.size();
    }
    @Override
    public FluidStack getFluidInTank(int tank) {
        return tanks.get(tank).tank().getFluidInTank(0);
    }
    @Override
    public int getTankCapacity(int tank) {
        return tanks.get(tank).tank().getTankCapacity(0);
    }
    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return tanks.get(tank).tank().isFluidValid(stack);
    }
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        int filled = 0;
        FluidStack remaining = resource.copy();
        for (FluidTankWithData tank : tanks) {
            if (remaining.isEmpty()) break;
            if (automation && tank.type() == SlotType.OUTPUT) continue;
            int accepted = tank.tank().fill(remaining, action);
            filled += accepted;
            remaining.shrink(accepted);
        }
        return filled;
    }
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        for (FluidTankWithData tank : tanks) {
            if (automation && tank.type() != SlotType.OUTPUT) continue;
            FluidStack drained = tank.tank().drain(resource, action);
            if (!drained.isEmpty()) return drained;
        }
        return FluidStack.EMPTY;
    }
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (FluidTankWithData tank : tanks) {
            if (automation && tank.type() != SlotType.OUTPUT) continue;
            FluidStack drained = tank.tank().drain(maxDrain, action);
            if (!drained.isEmpty()) return drained;
        }
        return FluidStack.EMPTY;
    }
    public record FluidTankWithData(FluidTank tank, SlotType type) {}
}
