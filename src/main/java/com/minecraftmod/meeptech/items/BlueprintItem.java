package com.minecraftmod.meeptech.items;

import java.util.List;

import com.minecraftmod.meeptech.ModDataComponents;
import com.minecraftmod.meeptech.logic.BlueprintData;
import com.minecraftmod.meeptech.logic.MachineType;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class BlueprintItem extends Item {
    public BlueprintItem(Properties properties) {
        super(properties);
    }
    @Override
    public Component getName(ItemStack stack) {
        BlueprintData data = stack.get(ModDataComponents.BLUEPRINT_DATA.get());
        if (data != null) {
            if (data.machineId() != null && !data.machineId().isEmpty()) {
                return Component.translatable("item.meeptech.blueprint.empty");
            }
        }
        return Component.translatable("item.meeptech.blueprint.blank");
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        BlueprintData data = stack.get(ModDataComponents.BLUEPRINT_DATA.get());
        if (data != null) {
            MachineType type = data.getMachineType();
            if (type != null) {
                tooltipComponents.add(Component.translatable("meeptech.misc.machine_type_tooltip").withStyle(ChatFormatting.GRAY)
                    .append(Component.translatable(type.getTranslationKey()).withStyle(ChatFormatting.GRAY)));
            }
        }
    }
}
