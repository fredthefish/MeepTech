package com.minecraftmod.meeptech.logic.material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.world.item.Items;

public class ModMaterials {
    public static final HashMap<MaterialStat, String> MATERIAL_STAT_TRANSLATION_KEYS = new HashMap<>();

    public static final ArrayList<Material> MATERIALS = new ArrayList<>();
    public static final ArrayList<MaterialForm> MATERIAL_FORMS = new ArrayList<>();

    public static final Material IRON = addMaterial("iron");
    public static final Material BRICK = addMaterial("brick");

    public static final MaterialForm BASE = addMaterialForm("base");
    public static final MaterialForm PLATE = addMaterialForm("plate");
    public static final MaterialForm BOX = addMaterialForm("box");
    public static final MaterialForm HULL = addMaterialForm("hull");

    public static void initializeMaterials() {
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.ThermalConductivity, "meeptech.materialStat.thermal_conductivity");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.ThermalResistance, "meeptech.materialStat.thermal_resistance");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.Flammability, "meeptech.materialStat.flammability");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.MeltingPoint, "meeptech.materialStat.melting_point");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.TensileStrength, "meeptech.materialStat.tensile_strength");

        //Iron
        IRON.setForm(BASE, Items.IRON_INGOT);
        IRON.setGeneratedForms(List.of(PLATE, BOX, HULL));
        IRON.setFormTexture(HULL, "metal");
        IRON.setColor(0xFFFFFFFF);
        IRON.setThermalConductivity(60);
        IRON.setMeltingPoint(1800);
        IRON.setTensileStrength(600);

        BRICK.setForm(BASE, Items.BRICK);
        BRICK.setGeneratedForms(List.of(PLATE, BOX, HULL));
        BRICK.setFormTexture(PLATE, "rough_plate");
        BRICK.setFormTexture(HULL, "brick");
        BRICK.setColor(0xFFb75a40);
        BRICK.setThermalConductivity(1);
        BRICK.setMeltingPoint(1800);
        BRICK.setTensileStrength(5);
    }

    public static Material addMaterial(String materialId) {
        Material material = new Material(materialId);
        MATERIALS.add(material);
        return material;
    }
    public static MaterialForm addMaterialForm(String materialFormId) {
        MaterialForm materialForm = new MaterialForm(materialFormId);
        MATERIAL_FORMS.add(materialForm);
        return materialForm;
    }
    public static Material getMaterial(String materialId) {
        for (Material material : MATERIALS) {
            if (material.getId().equals(materialId)) return material;
        }
        return null;
    }
}