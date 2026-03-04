package com.minecraftmod.meeptech.logic;

import java.util.HashSet;

import com.minecraftmod.meeptech.ModItems;

import net.minecraft.world.item.Items;

public class ModMaterials {
    public static final HashSet<Material> MATERIALS = new HashSet<Material>();

    public static final Material IRON = addMaterial(new Material("iron"));
    
    public static void InitializeMaterials() {
        IRON.addForm(MaterialForm.Base, Items.IRON_INGOT);
        IRON.addForm(MaterialForm.Plate, ModItems.IRON_PLATE.get());
    }

    public static Material addMaterial(Material material) {
        MATERIALS.add(material);
        return material;
    }
}