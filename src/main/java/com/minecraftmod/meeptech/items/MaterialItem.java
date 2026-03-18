package com.minecraftmod.meeptech.items;

import com.minecraftmod.meeptech.logic.material.Material;
import com.minecraftmod.meeptech.logic.material.MaterialForm;
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
        Material material = itemData.getMaterial();
        MaterialForm form = itemData.getForm();
        if (itemData.getForm().getPrefix()) return Component.translatable(material.getFormTranslationKey(form)).append(" ")
            .append(Component.translatable(material.getTranslationKey()));
        else return Component.translatable(material.getTranslationKey()).append(" ")
            .append(Component.translatable(material.getFormTranslationKey(form)));
    }
}
