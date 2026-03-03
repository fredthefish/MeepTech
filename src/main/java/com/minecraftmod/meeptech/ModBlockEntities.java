package com.minecraftmod.meeptech;

import java.util.function.Supplier;

import com.minecraftmod.meeptech.Blocks.PrimitiveWorkstationBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MeepTech.MODID);

    public static final Supplier<BlockEntityType<PrimitiveWorkstationBlockEntity>> PRIMITIVE_WORKSTATION_BE = 
        BLOCK_ENTITY_TYPES.register("primitive_workstation_be", () -> BlockEntityType.Builder.of(
            PrimitiveWorkstationBlockEntity::new,
            ModBlocks.PRIMITIVE_WORKSTATION.get()
        ).build(null));
}
