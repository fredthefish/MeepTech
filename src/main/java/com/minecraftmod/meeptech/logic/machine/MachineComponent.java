package com.minecraftmod.meeptech.logic.machine;

import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialStat;

public class MachineComponent extends MachineAttribute {
    private MachineStat machineStat;
    private MaterialStat materialStat;
    private ComponentCalculations calculations;

    public MachineComponent(String id, MachineStat machineStat, MaterialStat materialStat, ComponentCalculations calculations) {
        super(id);
        this.machineStat = machineStat;
        this.materialStat = materialStat;
        this.calculations = calculations;
    }
    public MachineStat getMachineStat() {
        return machineStat;
    }
    public Object performCalculations(Material material) {
        Object stat = material.getStat(materialStat);
        return calculations.calculations(stat);
    }
}