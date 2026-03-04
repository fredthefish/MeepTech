package com.minecraftmod.meeptech.logic;

import java.util.HashMap;

import net.minecraft.world.item.Item;

public class Material {
    private String name;
    private HashMap<MaterialForm, Item> forms = new HashMap<>();

    public Material(String name) {
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
    public String getName() {
        return name;
    }
}
