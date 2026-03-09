package com.minecraftmod.meeptech.items;

import java.util.HashMap;
import java.util.Map;

import com.minecraftmod.meeptech.ModItems;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModuleItems {
    public static final Map<String, DeferredItem<Item>> MODULES = new HashMap<>();
    public static void registerModules() {
        addModuleItem("template_basic");
        addModuleItem("machine_core_smelter");
        addModuleItem("heating_core_solid_fuel");
    }
    private static DeferredItem<Item> addModuleItem(String id) {
        DeferredItem<Item> item = ModItems.ITEMS.registerSimpleItem("module_" + id);
        MODULES.put(id, item);
        return item;
    }
}
