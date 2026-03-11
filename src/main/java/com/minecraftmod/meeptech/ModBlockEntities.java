package com.minecraftmod.meeptech;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.blocks.EngineeringStationBlockEntity;
import com.minecraftmod.meeptech.blocks.MaterialWorkstationBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MeepTech.MODID);

    public static final Supplier<BlockEntityType<MaterialWorkstationBlockEntity>> MATERIAL_WORKSTATION_BE = 
        BLOCK_ENTITY_TYPES.register("primitive_workstation_be", () -> BlockEntityType.Builder.of(
            MaterialWorkstationBlockEntity::new,
            ModBlocks.MATERIAL_WORKSTATION.get()
        ).build(null));
    public static final Supplier<BlockEntityType<EngineeringStationBlockEntity>> ENGINEERING_STATION_BE = 
        BLOCK_ENTITY_TYPES.register("engineering_station_be", () -> BlockEntityType.Builder.of(
            EngineeringStationBlockEntity::new,
            ModBlocks.ENGINEERING_STATION.get()
        ).build(null));
    public static final Supplier<BlockEntityType<BaseMachineBlockEntity>> BASE_MACHINE_BE =
        BLOCK_ENTITY_TYPES.register("base_machine_be", () -> BlockEntityType.Builder.of(
            BaseMachineBlockEntity::new,
            ModBlocks.BRICK_HULL.get() //Additional valid blocks can be added here.
        ).build(null));
}
