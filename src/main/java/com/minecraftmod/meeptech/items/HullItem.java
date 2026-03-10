package com.minecraftmod.meeptech.items;

import com.minecraftmod.meeptech.ModDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class HullItem extends BlockItem {

    public HullItem(Block block, Properties properties) {
        super(block, properties);
    }
    @Override
    public Component getName(ItemStack stack) {
        MachineConfigData data = stack.get(ModDataComponents.MACHINE_CONFIG_DATA.get());
        if (data != null) {
            //TODO: IMPLEMENT LOGIC TO SEE IF THE MACHINE IS PARTIALLY FILLED OR FULLY FILLED.
        }
        return Component.translatable("item.meeptech.hull.empty").append(" ").append(super.getName(stack));
    }
    //TODO: IMPLEMENT LOGIC TO ADD A TOOLTIP.
}
