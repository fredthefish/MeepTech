package com.minecraftmod.meeptech.logic.machine;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.ui.UIElement;

public class MachineType extends MachineAttribute {
    EnergySourceType energySource;
    List<UIElement> uiElements = new ArrayList<>();
    public MachineType(String id, EnergySourceType energySource) {
        super(id);
        this.energySource = energySource;
    }
    public String getTranslationKey() {
        return "meeptech.moduleType." + getId();
    }
    public void addUIElement(UIElement element) {
        uiElements.add(element);
    }
    public List<UIElement> getUIElements() {
        return uiElements;
    }
}