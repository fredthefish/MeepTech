package com.minecraftmod.meeptech.items;

import com.minecraftmod.meeptech.blocks.OreStoneType;
import com.minecraftmod.meeptech.logic.material.MaterialItemData;
import com.minecraftmod.meeptech.registries.ModDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class OreItem extends BlockItem {
    public OreItem(Block block, Properties properties) {
        super(block, properties);
    }
    @Override
    public Component getName(ItemStack stack) {
        OreStoneType stoneType = stack.get(ModDataComponents.STONE_TYPE.get());
        MaterialItemData itemData = new MaterialItemData(stack.getItem());
        MutableComponent component = Component.translatable(itemData.getMaterial().getTranslationKey());
        if (stoneType != OreStoneType.STONE) 
            component.append(Component.literal(" ").append(Component.translatable("meeptech.stoneType." + stoneType.getSerializedName())));
        component.append(" Ore");
        return component;
    }
}
