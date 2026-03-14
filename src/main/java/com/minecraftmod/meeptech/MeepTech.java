package com.minecraftmod.meeptech;

import com.minecraftmod.meeptech.items.ModuleItems;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.minecraft.resources.ResourceLocation;

@Mod(MeepTech.MODID)
public class MeepTech {
    public static final String MODID = "meeptech";

    public MeepTech(IEventBus modEventBus, ModContainer modContainer) {
        ModuleItems.registerModuleItems();
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        guideme.Guide.builder(ResourceLocation.fromNamespaceAndPath(MODID, "manual")).build();
    }
}
