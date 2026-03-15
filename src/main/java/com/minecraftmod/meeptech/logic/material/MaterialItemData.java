package com.minecraftmod.meeptech.logic.material;

import java.util.HashMap;

import com.minecraftmod.meeptech.ModMaterials;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class MaterialItemData {
    private Material material = null;
    private MaterialForm form = null;

    public MaterialItemData(Item item) {
        for (Material material : ModMaterials.MATERIALS) {
            HashMap<MaterialForm, ItemLike> forms = material.getForms();
            for (MaterialForm form : forms.keySet()) {
                if (forms.get(form).asItem().equals(item)) {
                    this.material = material;
                    this.form = form;
                    return;
                }
            }
        }
    }
    public Material getMaterial() {
        return material;
    }
    public MaterialForm getForm() {
        return form;
    }
}
