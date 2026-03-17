package com.minecraftmod.meeptech.items;

import com.minecraftmod.meeptech.logic.material.MaterialItemData;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MaterialItem extends Item {
    public MaterialItem(Properties properties) {
        super(properties);
    }
    @Override
    public Component getName(ItemStack stack) {
        MaterialItemData itemData = new MaterialItemData(stack.getItem());
        if (itemData.getForm().getPrefix()) return Component.translatable(itemData.getForm().getTranslationKey()).append(" ")
            .append(Component.translatable(itemData.getMaterial().getTranslationKey()));
        else return Component.translatable(itemData.getMaterial().getTranslationKey()).append(" ")
            .append(Component.translatable(itemData.getForm().getTranslationKey()));
    }
}
