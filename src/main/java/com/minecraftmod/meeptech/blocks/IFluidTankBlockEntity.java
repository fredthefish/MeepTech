package com.minecraftmod.meeptech.blocks;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public interface IFluidTankBlockEntity {
    public FluidTank getTank(int index);
    public IFluidHandler getFluidHandler();
    public IFluidHandler getAutomationFluidHandler();
}
