package com.minecraftmod.meeptech;

import java.util.ArrayList;
import java.util.HashMap;

import com.minecraftmod.meeptech.logic.Material;
import com.minecraftmod.meeptech.logic.MaterialForm;
import com.minecraftmod.meeptech.logic.MaterialStat;

import net.minecraft.world.item.Items;

public class ModMaterials {
    public static final HashMap<MaterialForm, String> FORM_TRANSLATION_KEYS = new HashMap<>();
    public static final HashMap<MaterialStat, String> MATERIAL_STAT_TRANSLATION_KEYS = new HashMap<>();

    public static final ArrayList<Material> MATERIALS = new ArrayList<Material>();

    public static final Material IRON = addMaterial(new Material("iron"));
    
    public static void InitializeMaterials() {
        FORM_TRANSLATION_KEYS.put(MaterialForm.Base, "meeptech.materialForm.base");
        FORM_TRANSLATION_KEYS.put(MaterialForm.Plate, "meeptech.materialForm.plate");
        FORM_TRANSLATION_KEYS.put(MaterialForm.LargePlate, "meeptech.materialForm.large_plate");
        FORM_TRANSLATION_KEYS.put(MaterialForm.Box, "meeptech.materialForm.box");
        FORM_TRANSLATION_KEYS.put(MaterialForm.Hull, "meeptech.materialForm.hull");

        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.ThermalConductivity, "meeptech.materialStat.thermal_conductivity");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.ThermalResistance, "meeptech.materialStat.thermal_resistance");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.Flammability, "meeptech.materialStat.flammability");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.MeltingPoint, "meeptech.materialStat.melting_point");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.TensileStrength, "meeptech.materialStat.tensile_strength");

        //Iron
        IRON.addForm(MaterialForm.Base, Items.IRON_INGOT);
        IRON.addForm(MaterialForm.Plate, ModItems.IRON_PLATE.get());
        IRON.setThermalConductivity(60);
        IRON.setMeltingPoint(1100);
        IRON.setTensileStrength(600);
    }

    public static Material addMaterial(Material material) {
        MATERIALS.add(material);
        return material;
    }
}