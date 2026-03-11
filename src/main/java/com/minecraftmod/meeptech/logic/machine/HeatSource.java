package com.minecraftmod.meeptech.logic.machine;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.ui.UIElement;

public class HeatSource extends MachineAttribute {
    List<UIElement> uiElements = new ArrayList<>();
    public HeatSource(String id) {
        super(id);
    }
    public void addUIElement(UIElement element) {
        uiElements.add(element);
    }
    public List<UIElement> getUIElements() {
        return uiElements;
    }
}