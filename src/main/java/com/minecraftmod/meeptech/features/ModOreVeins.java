package com.minecraftmod.meeptech.features;

import java.util.ArrayList;

import com.minecraftmod.meeptech.logic.material.ModMaterials;

public class ModOreVeins {
    public static final ArrayList<OreVein> ORE_VEINS = new ArrayList<>();

    public static final OreVein COPPER_VEIN = 
        addOreVein("copper").addOre(ModMaterials.COPPER, 1).setData(16, 16, 0.75f, -64, 128);
    public static final OreVein IRON_VEIN =
        addOreVein("iron").addOre(ModMaterials.IRON, 1).setData(16, 16, 0.75f, -64, 128);
    public static final OreVein COAL_VEIN = 
        addOreVein("coal").addOre(ModMaterials.COAL, 1).setData(16, 16, 0.75f, -64, 128);

    public static OreVein addOreVein(String id) {
        OreVein vein = new OreVein(id);
        ORE_VEINS.add(vein);
        return vein;
    }
}
