package com.minecraftmod.meeptech.items;

import java.util.List;

import com.minecraftmod.meeptech.logic.machine.EnergySource;
import com.minecraftmod.meeptech.logic.machine.MachineAttribute;
import com.minecraftmod.meeptech.logic.machine.MachineConfigData;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.material.MaterialItemData;
import com.minecraftmod.meeptech.logic.module.ModModuleTypes;
import com.minecraftmod.meeptech.registries.ModDataComponents;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

public class HullItem extends BlockItem {

    public HullItem(Block block, Properties properties) {
        super(block, properties);
    }
    @Override
    public Component getName(ItemStack stack) {
        MachineConfigData data = stack.get(ModDataComponents.MACHINE_CONFIG_DATA.get());
        if (data != null && !data.isEmpty() && data.hasSubLayers()) {
            if (data.allSlotsFilled()) {
                MachineData machineData = data.toMachineData();
                if (machineData == null) return Component.literal("Unknown Machine");
                return Component.translatable(machineData.getBase().getTranslationKey()).append(" ")
                    .append(Component.translatable(machineData.getType().getTranslationKey()));
            } else {
                return Component.translatable("item.meeptech.hull.partial").append(" ").append(getTranslation(stack));
            }
        }
        return Component.translatable("item.meeptech.hull.empty").append(" ").append(getTranslation(stack));
    }
    public Component getTranslation(ItemStack stack) {
        MaterialItemData itemData = new MaterialItemData(stack.getItem());
        return Component.translatable(itemData.getMaterial().getTranslationKey())
            .append(" ").append(Component.translatable(itemData.getForm().getTranslationKey()));
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        MachineConfigData data = stack.get(ModDataComponents.MACHINE_CONFIG_DATA.get());
        if (data == null) return;
        MachineData machineData = data.toMachineData();
        if (machineData == null) return;
        tooltipComponents.add(Component.translatable(ModModuleTypes.SLOT_MACHINE_BASE.getTranslationKey())
            .append(": ").append(Component.translatable("meeptech.moduleType." + machineData.getBase().getId())));
        tooltipComponents.add(Component.translatable(ModModuleTypes.SLOT_MACHINE_CORE.getTranslationKey())
            .append(": ").append(Component.translatable("meeptech.moduleType." + machineData.getType().getId())));
        MachineAttribute energySource = machineData.getEnergySource();
        if (energySource instanceof EnergySource heatSource) {
            tooltipComponents.add(Component.translatable(ModModuleTypes.SLOT_HEATING_CORE.getTranslationKey())
            .append(": ").append(Component.translatable("meeptech.moduleType." + heatSource.getId())));
        }
    }
}
