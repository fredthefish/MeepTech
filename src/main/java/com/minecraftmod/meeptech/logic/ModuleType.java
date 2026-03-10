package com.minecraftmod.meeptech.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.minecraftmod.meeptech.ModModuleTypes;
import com.minecraftmod.meeptech.ModTags;
import com.minecraftmod.meeptech.items.MachineConfigData;
import com.minecraftmod.meeptech.items.ModuleItems;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModuleType {
    private final String id;
    private final ModuleSlotType type;
    private final List<ModuleSlot> subSlots;
    private MaterialForm materialForm = null;
    public ModuleType(String id, ModuleSlotType type) {
        this.id = id;
        this.type = type;
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
    public void addSubSlot(String subSlotId, ModuleSlotType subSlotType) {
        subSlots.add(new ModuleSlot(subSlotId, subSlotType));
    }
    public MachineConfigData getEmptyMachineConfigData() {
        ArrayList<MachineConfigData> subModules = new ArrayList<>();
        for (int i = 0; i < getSubSlotCount(); i++) {
            subModules.add(MachineConfigData.EMPTY);
        }
        return new MachineConfigData(id, "", subModules);
    }
    public static MachineConfigData getMaterialMachineConfigData(String materialId) {
        return new MachineConfigData("", materialId, new ArrayList<>());
    }
    public static boolean itemFitsSlotType(ItemStack item, ModuleSlotType type) {
        switch (type) {
            case MachineBase:
                return item.is(ModTags.HULL_TAG);
            case FireboxSlot:
                return false;
            default:
                ModuleType moduleType = getModuleType(item);
                if (moduleType == null) return false;
                return moduleType.type == type;
        }
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
