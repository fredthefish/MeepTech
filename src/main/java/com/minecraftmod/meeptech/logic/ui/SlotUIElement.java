package com.minecraftmod.meeptech.logic.ui;

public class SlotUIElement {
    private SlotType type;
    private int id;
    private UIModule module;
    public SlotUIElement(SlotType type, int id, UIModule module) {
        this.type = type;
        this.id = id;
        this.module = module;
    }
    public SlotType getType() {
        return type;
    }
    public int getX() {
        return module.getX() + 3 + 17 * id;
    }
    public int getY() {
        return module.getY() + 9;
    }
}