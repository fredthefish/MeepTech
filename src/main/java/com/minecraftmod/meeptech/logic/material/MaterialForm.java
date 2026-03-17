package com.minecraftmod.meeptech.logic.material;

import java.util.ArrayList;

public class MaterialForm {
    public static final ArrayList<MaterialForm> MATERIAL_FORMS = new ArrayList<>();

    public static final MaterialForm ORE = addMaterialForm("ore");
    public static final MaterialForm RAW = addMaterialForm("raw", true);
    public static final MaterialForm BASE = addMaterialForm("base");
    public static final MaterialForm PLATE = addMaterialForm("plate");
    public static final MaterialForm BOX = addMaterialForm("box");
    public static final MaterialForm HULL = addMaterialForm("hull");

    private String id;
    private boolean prefix = false;
    public MaterialForm(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public String getTranslationKey() {
        return "meeptech.materialForm." + id;
    }
    public void setPrefix() {
        prefix = true;
    }
    public boolean getPrefix() {
        return prefix;
    }
    public static MaterialForm addMaterialForm(String materialFormId) {
        MaterialForm materialForm = new MaterialForm(materialFormId);
        MATERIAL_FORMS.add(materialForm);
        return materialForm;
    }
    public static MaterialForm addMaterialForm(String materialFormId, boolean prefix) {
        MaterialForm materialForm = new MaterialForm(materialFormId);
        materialForm.setPrefix();
        MATERIAL_FORMS.add(materialForm);
        return materialForm;
    }
}
