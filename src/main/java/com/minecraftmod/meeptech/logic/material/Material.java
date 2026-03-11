package com.minecraftmod.meeptech.logic.material;

import java.math.BigDecimal;
import java.util.HashMap;

import net.minecraft.world.item.Item;

public class Material {
    private String id;
    private String translationKey;
    private HashMap<MaterialForm, Item> forms = new HashMap<>();

    private Double thermalConductivity;
    private Double thermalResistance;
    private boolean flammable = false;
    private Double meltingPoint;
    private Double tensileStrength;
    private int color;

    public Material(String id) {
        this.id = id;
        this.translationKey = "meeptech.material." + id;
    }

    public void addForm(MaterialForm form, Item item) {
        forms.put(form, item);
    }
    public Item getForm(MaterialForm form) {
        return forms.get(form);
    }
    public boolean hasForm(MaterialForm form) {
        return forms.containsKey(form);
    }
    public HashMap<MaterialForm, Item> getForms() {
        return new HashMap<MaterialForm, Item>(forms);
    }
    public String getId() {
        return id;
    }
    public String getTranslationKey() {
        return translationKey;
    }

    public Double getThermalConductivity() {
        if (thermalConductivity != null) return thermalConductivity;
        if (thermalResistance != null) return 1 / thermalResistance;
        return null;
    }
    public Double getThermalResistance() {
        if (thermalResistance != null) return thermalResistance;
        if (thermalConductivity != null) return 1 / thermalConductivity;
        return null;
    }
    public boolean isFlammable() {
        return flammable;
    }
    public Double getMeltingPoint() {
        if (meltingPoint != null) return meltingPoint;
        return null;
    }
    public Double getTensileStrength() {
        if (tensileStrength != null) return tensileStrength;
        return null;
    }
    public void setThermalConductivity(double thermalConductivity) {
        this.thermalConductivity = thermalConductivity;
        this.thermalResistance = null;
    }
    public void setThermalResistance(double thermalResistance) {
        this.thermalResistance = thermalResistance;
        this.thermalConductivity = null;
    }
    public void setFlammable(boolean flammable) {
        this.flammable = flammable;
        this.meltingPoint = null;
    }
    public void setMeltingPoint(double meltingPoint) {
        this.meltingPoint = meltingPoint;
        this.flammable = false;
    }
    public void setTensileStrength(double tensileStrength) {
        this.tensileStrength = tensileStrength;
    }
    public Object getStat(MaterialStat stat) {
        switch (stat) {
            case ThermalConductivity:
                return getThermalConductivity();
            case ThermalResistance:
                return getThermalResistance();
            case Flammability:
                return isFlammable();
            case MeltingPoint:
                return getMeltingPoint();
            case TensileStrength:
                return getTensileStrength();
        }
        return null;
    }
    public String getStatString(MaterialStat stat) {
        switch (stat) {
            case ThermalConductivity:
                Double thermalConductivity = getThermalConductivity();
                if (thermalConductivity != null) return roundNumber(thermalConductivity, 2) + " W/(m K)";
                else return "N/A";
            case ThermalResistance:
                Double thermalResistance = getThermalResistance();
                if (thermalResistance != null) return roundNumber(thermalResistance, 2) + " m K/W";
                else return "N/A";
            case Flammability:
                return Boolean.toString(isFlammable());
            case MeltingPoint:
                Double meltingPoint = getMeltingPoint();
                if (meltingPoint != null) return meltingPoint.intValue() + " K";
                else return "N/A";
            case TensileStrength:
                Double tensileStrength = getTensileStrength();
                if (tensileStrength != null) return tensileStrength.intValue() + " MPa";
                else return "N/A";
        }
        return "ERROR";
    }
    private String roundNumber(double num, int precision) {
        BigDecimal bd = new BigDecimal(num);
        return String.format("%."+precision+"G", bd);
    }
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = 0xFF000000 + color;
    }
}
