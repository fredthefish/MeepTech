package com.minecraftmod.meeptech.registries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.blocks.EngineeringStationBlockEntity;
import com.minecraftmod.meeptech.blocks.MaterialWorkstationBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MeepTech.MODID);
    public static final Map<DeferredBlock<Block>, Supplier<BlockEntityType<BaseMachineBlockEntity>>> HULL_BLOCK_ENTITIES = new HashMap<>();

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
    static {
        for (DeferredBlock<Block> block : ModBlocks.HULL_BLOCKS.values()) {
            Supplier<BlockEntityType<BaseMachineBlockEntity>> hullBlockEntity = BLOCK_ENTITY_TYPES.register(block.getId().getPath() + "_be", 
                () -> BlockEntityType.Builder.of((pos, state) -> new BaseMachineBlockEntity(pos, state, block.get()), block.get()).build(null));
            HULL_BLOCK_ENTITIES.put(block, hullBlockEntity);
        }
    }
}
