package com.minecraftmod.meeptech.items;

import java.util.HashMap;
import java.util.Map;

import com.minecraftmod.meeptech.registries.ModItems;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModuleItems {
    public static final Map<String, DeferredItem<Item>> MODULES = new HashMap<>();
    public static final Map<String, DeferredItem<Item>> MODULE_ITEMS = new HashMap<>(); //Non-hulls.
    public static DeferredItem<Item> BASIC_TEMPLATE;
    public static DeferredItem<Item> SMELTER_CORE;
    public static DeferredItem<Item> SOLID_FUEL_CORE;
    public static void registerModuleItems() {
        BASIC_TEMPLATE = addModuleItem("template_basic");
        SMELTER_CORE = addModuleItem("machine_core_smelter");
        SOLID_FUEL_CORE = addModuleItem("heating_core_solid_fuel");
    }
    private static DeferredItem<Item> addModuleItem(String id) {
        DeferredItem<Item> item = ModItems.ITEMS.registerSimpleItem("module_" + id);
        MODULES.put(id, item);
        MODULE_ITEMS.put(id, item);
        return item;
    }
}
