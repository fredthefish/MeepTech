package com.minecraftmod.meeptech.logic.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.minecraftmod.meeptech.items.ModuleItems;
import com.minecraftmod.meeptech.logic.machine.MachineAttribute;
import com.minecraftmod.meeptech.logic.machine.MachineBase;
import com.minecraftmod.meeptech.logic.machine.MachineConfigData;
import com.minecraftmod.meeptech.logic.material.MaterialForm;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModuleType {
    private final String id;
    private final ModuleSlotType type;
    private final MachineAttribute attribute;
    private final List<ModuleSlot> subSlots;
    private ModuleSlotType upgradeType;
    private int moduleTier = 0;
    private MaterialForm materialForm = null;
    public ModuleType(String id, ModuleSlotType type, MachineAttribute attribute) {
        this.id = id;
        this.type = type;
        this.attribute = attribute;
        this.subSlots = new ArrayList<>();
    }
    public String getId() {
        return id;
    }
    public String getTranslationKey() {
        return "meeptech.moduleType." + id;
    }
    public Item getItem() {
        if (!ModuleItems.MODULES.containsKey(id)) return null;
        DeferredItem<Item> deferredItem = ModuleItems.MODULES.get(id);
        if (deferredItem == null) return null;
        return ModuleItems.MODULES.get(id).get();
    }
    public ModuleSlotType getType() {
        return type;
    }
    public ModuleSlot getSubSlot(int index) {
        return subSlots.get(index);
    }
    public int getSubSlotCount() {
        return subSlots.size();
    }
    public MachineAttribute getAttribute() {
        return attribute;
    }
    public void setUpgradeType(ModuleSlotType upgradeType) {
        this.upgradeType = upgradeType;
    }
    public ModuleSlotType getUpgradeType() {
        return upgradeType;
    }
    public void addSubSlot(String subSlotId, ModuleSlotType subSlotType) {
        subSlots.add(new ModuleSlot(subSlotId, subSlotType));
    }
    public void setModuleTier(int tier) {
        moduleTier = tier;
    }
    public int getModuleTier() {
        return moduleTier;
    }
    public MachineConfigData getEmptyMachineConfigData() {
        ArrayList<MachineConfigData> subModules = new ArrayList<>();
        for (int i = 0; i < getSubSlotCount(); i++) {
            subModules.add(MachineConfigData.EMPTY);
        }
        int upgradeSlots = 0;
        if (attribute instanceof MachineBase machineBase) upgradeSlots = machineBase.getUpgradeSlots();
        return new MachineConfigData(type.getId(), id, "", upgradeSlots, subModules);
    }
    public static MachineConfigData getMaterialMachineConfigData(ModuleSlotType slotType, String materialId) {
        return new MachineConfigData(slotType.getId(), "", materialId, 0, new ArrayList<>());
    }
    public static ModuleType getModuleType(ItemStack item) {
        for (Entry<String, DeferredItem<Item>> pair : ModuleItems.MODULES.entrySet()) {
            if (item.is(pair.getValue())) return ModModuleTypes.getModuleType(pair.getKey());
        }
        return null;
    }
    public void setMaterialForm(MaterialForm form) {
        this.materialForm = form;
    }
    public MaterialForm getMaterialForm() {
        return materialForm;
    }
    public void setAssociatedItem(DeferredItem<Item> item) {
        ModuleItems.MODULES.put(id, item);
    }
}
