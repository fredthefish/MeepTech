package com.minecraftmod.meeptech.logic.ui;

public class SlotUIElement {
    private SlotType type;
    private int moduleId;
    private UIModule module;
    public SlotUIElement(SlotType type, int moduleId, UIModule module) {
        this.type = type;
        this.moduleId = moduleId;
        this.module = module;
    }
    public SlotType getType() {
        return type;
    }
    public int getX() {
        return module.getX() + 3 + 17 * moduleId;
    }
    public int getY() {
        return module.getY() + 13;
    }
}