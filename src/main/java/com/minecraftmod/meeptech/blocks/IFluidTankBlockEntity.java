package com.minecraftmod.meeptech.blocks;

import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public interface IFluidTankBlockEntity {
    public FluidTank getTank(int index);
}
