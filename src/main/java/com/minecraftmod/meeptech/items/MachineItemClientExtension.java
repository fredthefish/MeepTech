package com.minecraftmod.meeptech.items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class MachineItemClientExtension implements IClientItemExtensions {
    private final MachineItemRenderer itemRenderer = new MachineItemRenderer();
    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return itemRenderer;
    }
}
