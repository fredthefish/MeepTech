package com.minecraftmod.meeptech.logic;

import java.util.HashMap;

import net.minecraft.world.item.Item;

public class ItemData {
    private Material material = null;
    private MaterialForm form = null;

    public ItemData(Item item) {
        for (Material material : ModMaterials.MATERIALS) {
            HashMap<MaterialForm, Item> forms = material.getForms();
            for (MaterialForm form : forms.keySet()) {
                if (forms.get(form).equals(item)) {
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
