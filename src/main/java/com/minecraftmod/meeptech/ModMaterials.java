package com.minecraftmod.meeptech;

import java.util.ArrayList;

import com.minecraftmod.meeptech.logic.Material;
import com.minecraftmod.meeptech.logic.MaterialForm;

import net.minecraft.world.item.Items;

public class ModMaterials {
    public static final ArrayList<Material> MATERIALS = new ArrayList<Material>();

    public static final Material IRON = addMaterial(new Material("iron", "Iron"));
    
    public static void InitializeMaterials() {
        //Iron Forms
        IRON.addForm(MaterialForm.Base, Items.IRON_INGOT);
        IRON.addForm(MaterialForm.Plate, ModItems.IRON_PLATE.get());
        //Iron Statistics
        IRON.setThermalConductivity(60);
        IRON.setMeltingPoint(1100);
        IRON.setTensileStrength(600);
    }

    public static Material addMaterial(Material material) {
        MATERIALS.add(material);
        return material;
    }
}