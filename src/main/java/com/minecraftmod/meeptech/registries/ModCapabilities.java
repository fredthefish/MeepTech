package com.minecraftmod.meeptech.registries;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = MeepTech.MODID)
public class ModCapabilities {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (Supplier<BlockEntityType<BaseMachineBlockEntity>> blockEntity : ModBlockEntities.HULL_BLOCK_ENTITIES.values())
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, blockEntity.get(),
            (be, side) -> be.getAutomationHandler());
    }
}