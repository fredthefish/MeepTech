package com.minecraftmod.meeptech.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HammerItem extends Item {
    public HammerItem(Properties properties) {
        super(properties);
    }
    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack remainder = stack.copy();
        // remainder.setDamageValue(remainder.getDamageValue() + 1);
        // if (remainder.getDamageValue() >= remainder.getMaxDamage()) {
        //     return ItemStack.EMPTY;
        // }
        return remainder;
    }
}
