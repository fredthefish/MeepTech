package com.minecraftmod.meeptech.logic.material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.minecraftmod.meeptech.logic.module.ModModuleData;

import net.minecraft.world.item.Items;

public class ModMaterials {
    public static final HashMap<MaterialStat, String> MATERIAL_STAT_TRANSLATION_KEYS = new HashMap<>();

    public static final ArrayList<Material> MATERIALS = new ArrayList<>();

    public static final Material IRON = addMaterial("iron");
    public static final Material BRICK = addMaterial("brick");
    public static final Material COPPER = addMaterial("copper");
    public static final Material COAL = addMaterial("coal");
    public static final Material TIN = addMaterial("tin");
    public static final Material BRONZE = addMaterial("bronze");

    public static void initializeMaterials() {
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.ThermalConductivity, "meeptech.materialStat.thermal_conductivity");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.ThermalResistance, "meeptech.materialStat.thermal_resistance");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.Flammability, "meeptech.materialStat.flammability");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.MeltingPoint, "meeptech.materialStat.melting_point");
        MATERIAL_STAT_TRANSLATION_KEYS.put(MaterialStat.TensileStrength, "meeptech.materialStat.tensile_strength");

        //Iron
        IRON.setForm(MaterialForm.BASE, Items.IRON_INGOT);
        IRON.setForm(MaterialForm.RAW, Items.RAW_IRON);
        IRON.setGeneratedForms(List.of(MaterialForm.PLATE, MaterialForm.BOX, MaterialForm.HULL, MaterialForm.ORE));
        IRON.setFormTexture(MaterialForm.HULL, "metal");
        IRON.setFormTexture(MaterialForm.ORE, "dull");
        IRON.setHullBase(ModModuleData.BASE_BASIC);
        IRON.setColor(0xFFe8e8e8);
        IRON.setThermalConductivity(60);
        IRON.setMeltingPoint(1800);
        IRON.setTensileStrength(600);

        BRICK.setForm(MaterialForm.BASE, Items.BRICK);
        BRICK.setGeneratedForms(List.of(MaterialForm.PLATE, MaterialForm.BOX, MaterialForm.HULL));
        BRICK.setFormTexture(MaterialForm.PLATE, "rough_plate");
        BRICK.setFormTexture(MaterialForm.HULL, "brick");
        BRICK.setHullBase(ModModuleData.BASE_BASIC);
        BRICK.setColor(0xFFb75a40);
        BRICK.setThermalConductivity(1);
        BRICK.setMeltingPoint(1800);
        BRICK.setTensileStrength(5);

        COPPER.setForm(MaterialForm.BASE, Items.COPPER_INGOT);
        COPPER.setForm(MaterialForm.RAW, Items.RAW_COPPER);
        COPPER.setGeneratedForms(List.of(MaterialForm.PLATE, MaterialForm.BOX, MaterialForm.HULL, MaterialForm.ORE));
        COPPER.setFormTexture(MaterialForm.HULL, "metal");
        COPPER.setFormTexture(MaterialForm.ORE, "copper");
        COPPER.setHullBase(ModModuleData.BASE_BASIC);
        COPPER.setColor(0xFFe77c56);
        COPPER.setThermalConductivity(400);
        COPPER.setMeltingPoint(1350);
        COPPER.setTensileStrength(400);

        COAL.setForm(MaterialForm.BASE, Items.COAL);
        COAL.setGeneratedForms(List.of(MaterialForm.ORE, MaterialForm.RAW));
        COAL.setFormTexture(MaterialForm.ORE, "coal");
        COAL.setFormTexture(MaterialForm.RAW, "fine_raw_ore");
        COAL.setColor(0xFF2d2d2d);
        COAL.setOreMultiplier(2);

        TIN.setGeneratedForms(List.of(MaterialForm.BASE, MaterialForm.RAW, MaterialForm.PLATE, MaterialForm.BOX, MaterialForm.HULL, MaterialForm.ORE));
        TIN.setFormTexture(MaterialForm.BASE, "dull_ingot");
        TIN.setFormTexture(MaterialForm.RAW, "dull_raw_ore");
        TIN.setFormTexture(MaterialForm.PLATE, "dull_plate");
        TIN.setFormTexture(MaterialForm.HULL, "metal");
        TIN.setFormTexture(MaterialForm.ORE, "dull");
        TIN.setColor(0xFFa0a1d6);
        TIN.setThermalConductivity(60);
        TIN.setMeltingPoint(500);
        TIN.setTensileStrength(30);

        BRONZE.setGeneratedForms(List.of(MaterialForm.BASE, MaterialForm.PLATE, MaterialForm.BOX, MaterialForm.HULL));
        BRONZE.setFormTexture(MaterialForm.BASE, "ingot");
        BRONZE.setFormTexture(MaterialForm.HULL, "metal");
        BRONZE.setColor(0xFFcd7f32);
        BRONZE.setFormTranslationKey(MaterialForm.BASE, "ingot");
        BRONZE.setThermalConductivity(25);
        BRONZE.setMeltingPoint(1250);
        BRONZE.setTensileStrength(600);
    }

    public static Material addMaterial(String materialId) {
        Material material = new Material(materialId);
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