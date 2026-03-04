package com.minecraftmod.meeptech.logic;

import java.util.HashMap;

import net.minecraft.world.item.Item;

public class Material {
    private String id;
    private String name;
    private HashMap<MaterialForm, Item> forms = new HashMap<>();

    private Double thermalConductivity;
    private Double thermalResistance;
    private boolean flammable = false;
    private Double meltingPoint;
    private Double tensileStrength;

    public Material(String id, String name) {
        this.id = id;
        this.name = name;
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
    public String getName() {
        return name;
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
    public Double meltingPoint() {
        if (meltingPoint != null) return meltingPoint;
        return null;
    }
    public Double tensileStrength() {
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
}
