package com.minecraftmod.meeptech.logic;

public class MachineAttributes {
    public static MachineBase BASE_BASIC = new MachineBase("basic");

    public static MachineType TYPE_SMELTER = new MachineType("smelter", EnergySourceType.Heat);

    public static HeatSource HEAT_SOURCE_SOLID_FUEL = new HeatSource("solid_fuel");

    public static MachineComponent COMPONENT_FIREBOX = new MachineComponent("firebox", MachineStat.Speed, MaterialStat.MeltingPoint, 
        (stat) -> {
            if (stat instanceof Double meltingPoint) {
                double baseTemperature = 1000;
                double rate = (meltingPoint * meltingPoint) / (baseTemperature * baseTemperature);
                return rate;
            }
            return null;
    });

    public static class MachineAttribute {
        String id;
        public MachineAttribute(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }
    }
    public static class MachineBase extends MachineAttribute {
        public MachineBase(String id) {
            super(id);
        }
    }
    public static class MachineType extends MachineAttribute {
        EnergySourceType energySource;
        public MachineType(String id, EnergySourceType energySource) {
            super(id);
            this.energySource = energySource;
        }
    }
    public static class HeatSource extends MachineAttribute {
        public HeatSource(String id) {
            super(id);
        }
    }
    public static class MachineComponent extends MachineAttribute {
        private MachineStat machineStat;
        private MaterialStat materialStat;
        private ComponentCalculations calculations;

        public MachineComponent(String id, MachineStat machineStat, MaterialStat materialStat, ComponentCalculations calculations) {
            super(id);
            this.machineStat = machineStat;
            this.materialStat = materialStat;
        }
        public MachineStat getMachineStat() {
            return machineStat;
        }
        public Object performCalculations(Material material) {
            Object stat = material.getStat(materialStat);
            return calculations.calculations(stat);
        }
    }
    public static enum EnergySourceType {
        Heat
    }
    public static enum MachineStat {
        Speed,
        Efficiency,
        HeatDecay
    }
}
