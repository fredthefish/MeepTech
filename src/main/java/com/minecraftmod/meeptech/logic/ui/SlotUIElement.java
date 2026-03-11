package com.minecraftmod.meeptech.logic.ui;

public class SlotUIElement extends UIElement {
    SlotType type;
    String id;
    public SlotUIElement(int x, int y, SlotType type, String id) {
        super(x, y);
        this.type = type;
        this.id = id;
    }
    public static int getX(int index) {
        return 9;
    }
    public static int getY(int index) {
        return 16 + 17 * index;
    }
}