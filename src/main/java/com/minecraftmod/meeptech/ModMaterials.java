package com.minecraftmod.meeptech;

import java.util.ArrayList;
import java.util.HashMap;

import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.logic.material.MaterialStat;

import net.minecraft.world.item.Items;

public class ModMaterials {
    public static final HashMap<MaterialForm, String> FORM_TRANSLATION_KEYS = new HashMap<>();
    public static final HashMap<MaterialStat, String> MATERIAL_STAT_TRANSLATION_KEYS = new HashMap<>();

    public static final ArrayList<Material> MATERIALS = new ArrayList<Material>();

    public static final Material IRON = addMaterial(new Material("iron"));
    public static final Material BRICK = addMaterial(new Material("brick"));
    
    public static void initializeMaterials() {
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
        IRON.addForm(MaterialForm.Plate, ModItems.IRON_PLATE.asItem());
        IRON.addForm(MaterialForm.Box, ModItems.IRON_BOX.asItem());
        IRON.setColor(0xFFFFFF);
        IRON.setThermalConductivity(60);
        IRON.setMeltingPoint(1800);
        IRON.setTensileStrength(600);

        BRICK.addForm(MaterialForm.Base, Items.BRICK);
        BRICK.addForm(MaterialForm.Plate, ModItems.BRICK_PLATE.asItem());
        BRICK.addForm(MaterialForm.Hull, ModBlocks.BRICK_HULL.asItem());
        BRICK.setColor(0x5a2d0f);
        BRICK.setThermalConductivity(1);
        BRICK.setMeltingPoint(1800);
        BRICK.setTensileStrength(5);
    }

    public static Material addMaterial(Material material) {
        MATERIALS.add(material);
        return material;
    }
    public static Material getMaterial(String materialId) {
        for (Material material : MATERIALS) {
            if (material.getId().equals(materialId)) return material;
        }
        return null;
    }
}