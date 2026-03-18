package com.minecraftmod.meeptech;

import com.minecraftmod.meeptech.items.MaterialItems;
import com.minecraftmod.meeptech.items.ModuleItems;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.registries.ModBlockEntities;
import com.minecraftmod.meeptech.registries.ModBlocks;
import com.minecraftmod.meeptech.registries.ModCommonConfig;
import com.minecraftmod.meeptech.registries.ModCreativeTabs;
import com.minecraftmod.meeptech.registries.ModDataComponents;
import com.minecraftmod.meeptech.registries.ModFeatures;
import com.minecraftmod.meeptech.registries.ModItems;
import com.minecraftmod.meeptech.registries.ModMenus;
import com.minecraftmod.meeptech.registries.ModPlacementModifiers;
import com.minecraftmod.meeptech.registries.ModServerConfig;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.ModContainer;
import net.minecraft.resources.ResourceLocation;

@Mod(MeepTech.MODID)
public class MeepTech {
    public static final String MODID = "meeptech";

    public MeepTech(IEventBus modEventBus, ModContainer modContainer) {
        ModuleItems.registerModuleItems();
        ModMaterials.initializeMaterials();
        MaterialItems.registerMaterialItems();
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModPlacementModifiers.PLACEMENT_MODIFIERS.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, ModCommonConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, ModServerConfig.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        guideme.Guide.builder(ResourceLocation.fromNamespaceAndPath(MODID, "manual")).build();
    }
}
