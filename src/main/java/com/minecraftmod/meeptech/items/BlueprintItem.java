package com.minecraftmod.meeptech.items;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.ModDataComponents;
import com.minecraftmod.meeptech.ModMaterials;
import com.minecraftmod.meeptech.logic.BlueprintData;
import com.minecraftmod.meeptech.logic.MachineComponent;
import com.minecraftmod.meeptech.logic.MachineType;
import com.minecraftmod.meeptech.logic.Material;

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
                if (data.getMaterialList() != null && !data.getMaterialList().isEmpty()) {
                    return Component.translatable("item.meeptech.blueprint.full");
                }
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
                tooltipComponents.add(Component.translatable("meeptech.ui.machine_type_tooltip").withStyle(ChatFormatting.GRAY)
                    .append(Component.translatable(type.getTranslationKey()).withStyle(ChatFormatting.GRAY)));
                List<String> materialIds = data.getMaterialList();
                if (materialIds != null && !materialIds.isEmpty()) {
                    ArrayList<MachineComponent> components = type.getComponents();
                    for (int i = 0; i < materialIds.size(); i++) {
                        if (!materialIds.get(i).isEmpty()) {
                            Material material = ModMaterials.getMaterial(materialIds.get(i));
                            if (material != null) {
                                tooltipComponents.add(
                                    Component.translatable(components.get(i).getTranslationKey()).withStyle(ChatFormatting.GRAY)
                                    .append(Component.literal(": ")).withStyle(ChatFormatting.GRAY)
                                    .append(Component.translatable(material.getTranslationKey()).withStyle(ChatFormatting.GRAY)));
                            }
                        }
                    }
                }
            }
        }
    }
}
