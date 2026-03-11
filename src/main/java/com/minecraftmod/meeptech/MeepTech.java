package com.minecraftmod.meeptech;

import com.minecraftmod.meeptech.items.ModuleItems;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.fml.ModContainer;

@Mod(MeepTech.MODID)
public class MeepTech {
    public static final String MODID = "meeptech";

    public MeepTech(IEventBus modEventBus, ModContainer modContainer) {
        ModuleItems.registerModules();
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ModMaterials.initializeMaterials();
        ModModuleTypes.initializeModuleTypes();
    }
}
