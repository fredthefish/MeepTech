package com.minecraftmod.meeptech.logic;

import java.util.ArrayList;

public class MachineComponent {
    private final String id;
    private final String name;
    private final MaterialForm form;
    private final int cost;
    private final ArrayList<MaterialStat> relevantStats;
    private final ArrayList<MachineStat> outputStats;
    private MachineCalculations calculations;

    public MachineComponent(String id, String name, MaterialForm form, int cost) {
        this.id = id;
        this.name = name;
        this.form = form;
        this.cost = cost;
        relevantStats = new ArrayList<MaterialStat>();
        outputStats = new ArrayList<MachineStat>();
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
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
    public Object[] runCalculations(Object[] inputs) {
        return calculations.calculations(inputs);
    }
}