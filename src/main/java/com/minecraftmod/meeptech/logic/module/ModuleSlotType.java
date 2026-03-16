package com.minecraftmod.meeptech.logic.module;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.material.MaterialForm;
import com.minecraftmod.meeptech.registries.ModTags;

import net.minecraft.world.item.ItemStack;

public class ModuleSlotType {
    private String id;
    private MaterialForm form = null;
    //Don't make self-referential loops, that would cause exceptions.
    private List<ModuleSlotType> subTypes = new ArrayList<>();
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
    public void addSubType(ModuleSlotType subType) {
        subTypes.add(subType);
    }
    public MaterialForm getMaterialForm() {
        return form;
    }
    public boolean itemFitsSlotType(ItemStack item) {
        if (this.getMaterialForm() != null) {
            return item.is(ModTags.FORM_TAGS.get(this.getMaterialForm()));
        } else {
            ModuleType moduleType = ModuleType.getModuleType(item);
            if (moduleType == null) return false;
            if (moduleType.getType() == this) return true;
            for (ModuleSlotType slotType : subTypes) if (slotType.itemFitsSlotType(item)) return true;
            return false;
        }
    }
}
