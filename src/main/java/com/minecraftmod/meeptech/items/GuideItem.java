package com.minecraftmod.meeptech.items;

import com.minecraftmod.meeptech.MeepTech;

import guideme.GuidesCommon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GuideItem extends Item {
    public GuideItem(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            ResourceLocation guideId = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "manual");
            GuidesCommon.openGuide(player, guideId);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
