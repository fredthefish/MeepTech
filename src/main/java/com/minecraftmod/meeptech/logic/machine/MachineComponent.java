package com.minecraftmod.meeptech.logic.machine;

import com.minecraftmod.meeptech.helpers.Formatting;
import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialStat;

import net.minecraft.network.chat.Component;

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
    public Component getString(Material material) {
        Object result = performCalculations(material);
        if (result instanceof Double doubleResult) {
            return Component.literal("- ").append(Component.translatable(machineStat.getTranslationKey()))
            .append(": " + Formatting.doubleFormatting(doubleResult) + "x");
        }
        return Component.literal("Placeholder");
    }
}