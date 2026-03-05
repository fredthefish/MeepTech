package com.minecraftmod.meeptech.logic;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.minecraftmod.meeptech.ModMaterials;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class MachineComponent {
    private final String id;
    private final String translationKey;
    private final MaterialForm form;
    private final int cost;
    private final ArrayList<MaterialStat> relevantStats;
    private final ArrayList<MachineStat> outputStats;
    private MachineCalculations calculations;

    public MachineComponent(String id, MaterialForm form, int cost) {
        this.id = id;
        this.translationKey = "meeptech.machineComponent." + id;
        this.form = form;
        this.cost = cost;
        relevantStats = new ArrayList<MaterialStat>();
        outputStats = new ArrayList<MachineStat>();
    }
    public String getId() {
        return id;
    }
    public String getTranslationKey() {
        return translationKey;
    }
    public MaterialForm getForm() {
        return form;
    }
    public int getCost() {
        return cost;
    }
    public ArrayList<MaterialStat> getRelevantStats() {
        return relevantStats;
    }
    public Component getRelevantStatsString() {
        String[] stringArray = new String[relevantStats.size()];
        for (int i = 0; i < stringArray.length; i++) {
            stringArray[i] = ModMaterials.MATERIAL_STAT_TRANSLATION_KEYS.get(relevantStats.get(i));
        }
        MutableComponent output = Component.literal("");
        for (int i = 0; i < stringArray.length; i++) {
            output.append(Component.translatable(stringArray[i]));
            if (i < stringArray.length - 1) output.append(Component.literal(", "));
        }
        return output;
    }
    public void addRelevantStat(MaterialStat stat) {
        relevantStats.add(stat);
    }
    public ArrayList<MachineStat> getOutputStats() {
        return outputStats;
    }
    public void addOutputStat(MachineStat stat) {
        outputStats.add(stat);
    }
    public void setCalculations(MachineCalculations calculations) {
        this.calculations = calculations;
    }
    public Object getStat(MachineStat stat, Material material) {
        Object[] inputs = new Object[this.relevantStats.size()];
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = material.getStat(relevantStats.get(i));
        }
        Object[] stats = calculations.calculations(inputs);
        for (int i = 0; i < outputStats.size(); i++) {
            if (outputStats.get(i) == stat) {
                return stats[i];
            }
        }
        return null;
    }
    public String getStatString(MachineStat stat, Material material) {
        Object statResult = getStat(stat, material);
        switch (stat) {
            case Speed:
                return roundNumber((double)statResult, 3) + "x";
            case Efficiency:
                return roundNumber((double)statResult, 3) + "x";
            case HeatDecay:
                return roundNumber((double)statResult, 3) + "x";
        }
        return null;
    }
    public String roundNumber(double num, int precision) {
        BigDecimal bd = new BigDecimal(num);
        return String.format("%."+precision+"G", bd);
    }
}