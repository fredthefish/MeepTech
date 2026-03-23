package com.minecraftmod.meeptech.registries;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.items.FluidCellItem;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

@EventBusSubscriber(modid = MeepTech.MODID)
public class ModCapabilities {
    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (Supplier<BlockEntityType<BaseMachineBlockEntity>> blockEntity : ModBlockEntities.HULL_BLOCK_ENTITIES.values())
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, blockEntity.get(),
            (be, side) -> be.getAutomationHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.FLUID_TANK_BE.get(), (be, side) -> be.getTank());
        event.registerItem(Capabilities.FluidHandler.ITEM, (stack, ctx) -> new FluidHandlerItemStack(
            ModDataComponents.FLUID_CELL_CONTENT, stack, ((FluidCellItem) stack.getItem()).getCapacity()), ModItems.FLUID_CELL.get());
    }
}