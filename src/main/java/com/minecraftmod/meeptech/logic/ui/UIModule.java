package com.minecraftmod.meeptech.logic.ui;

import java.util.ArrayList;

public class UIModule {
    private UIModuleType type;
    private String title;
    private ArrayList<SlotUIElement> slots;

    public UIModule(UIModuleType type, String title, int slotCount) {
        this.type = type;
        this.title = title;
        this.slots = new ArrayList<>();
        SlotType slotType;
        switch (type) {
            case UIModuleType.Input:
                slotType = SlotType.INPUT;
                break;
            case UIModuleType.Energy:
                slotType = SlotType.INPUT_FUEL;
                break;
            case UIModuleType.Output:
                slotType = SlotType.OUTPUT;
                break;
            default:
                slotType = null;
                break;
        }
        for (int i = 0; i < slotCount; i++) {
            slots.add(new SlotUIElement(slotType, i, this));
        }
    }
    public UIModuleType getType() {
        return type;
    }
    public String getTitle() {
        return title;
    }
    public int getSlotCount() {
        return slots.size();
    }
    public ArrayList<SlotUIElement> getSlots() {
        return slots;
    }
    public int getX() {
        if (type == UIModuleType.Input || type == UIModuleType.Energy) return 8;
        else if (type == UIModuleType.Output || type == UIModuleType.Recipe) return 89;
        throw new IllegalStateException("Invalid UI type");
    }
    public int getY() {
        if (type == UIModuleType.Input || type == UIModuleType.Output) return 19;
        else if (type == UIModuleType.Energy || type == UIModuleType.Recipe) return 69;
        throw new IllegalStateException("Invalid UI type");
    }
}
