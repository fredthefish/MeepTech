package com.minecraftmod.meeptech.logic.ui;

public class SlotUIElement {
    private final SlotType type;
    private final SlotClass slotClass;
    private final int moduleId;
    private final UIModule module;
    public SlotUIElement(SlotType type, SlotClass slotClass, int moduleId, UIModule module) {
        this.type = type;
        this.slotClass = slotClass;
        this.moduleId = moduleId;
        this.module = module;
    }
    public SlotType getType() {
        return type;
    }
    public SlotClass getSlotClass() {
        return slotClass;
    }
    public int getX() {
        return module.getX() + 3 + 17 * moduleId;
    }
    public int getY() {
        return module.getY() + 13;
    }
    public int getModuleId() {
        return moduleId;
    }
    public UIModule getModule() {
        return module;
    }
    public enum SlotClass {
        ITEM, FLUID
    }
}