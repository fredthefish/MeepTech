package com.minecraftmod.meeptech.logic.module;

import com.minecraftmod.meeptech.logic.material.MaterialForm;

public class ModuleSlotType {
    private String id;
    private MaterialForm form = null;
    public ModuleSlotType(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public String getTranslationKey() {
        return "meeptech.moduleSlotType." + id;
    }
    public void setMaterialForm(MaterialForm form) {
        this.form = form;
    }
    public MaterialForm getMaterialForm() {
        return form;
    }
}
