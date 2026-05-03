package com.minecraftmod.meeptech.items;

import java.util.HashMap;
import java.util.Map;

import com.minecraftmod.meeptech.registries.ModItems;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModuleItems {
    public static final Map<String, DeferredItem<Item>> MODULES = new HashMap<>();
    public static final Map<String, DeferredItem<Item>> MODULE_ITEMS = new HashMap<>(); //Non-hulls.
    
    public static DeferredItem<Item> TEMPLATE_STONE = addModuleItem("template_stone");
    public static DeferredItem<Item> TEMPLATE_BRONZE = addModuleItem("template_bronze");
    public static DeferredItem<Item> SMELTER_CORE = addModuleItem("machine_core_smelter");
    public static DeferredItem<Item> SOLID_FUEL_CORE = addModuleItem("heating_core_solid_fuel");
    public static DeferredItem<Item> SMELTER_BLASTING = addModuleItem("upgrade_smelter_blasting");
    public static DeferredItem<Item> ALLOYER_CORE = addModuleItem("machine_core_alloyer");
    public static DeferredItem<Item> STEAM_BOILER_CORE = addModuleItem("machine_core_steam_boiler");
    public static DeferredItem<Item> STEAM_ENERGY_CORE = addModuleItem("energy_core_steam");
    public static DeferredItem<Item> COKER_CORE = addModuleItem("machine_core_coker");
    public static DeferredItem<Item> PRESSER_CORE = addModuleItem("machine_core_presser");
    public static DeferredItem<Item> WATER_PUMPER_CORE = addModuleItem("machine_core_water_pumper");
    
    private static DeferredItem<Item> addModuleItem(String id) {
        DeferredItem<Item> item = ModItems.ITEMS.registerSimpleItem("module_" + id);
        MODULES.put(id, item);
        MODULE_ITEMS.put(id, item);
        return item;
    }
}