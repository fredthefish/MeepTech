package com.minecraftmod.meeptech.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.minecraftmod.meeptech.ModDataComponents;
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
        if (data != null && !data.isEmpty()) {
            if (data.allSlotsFilled()) {
                HashMap<ArrayList<String>, String> hashMap = data.toHashMap();
                String machineBase = hashMap.get(List.of());
                String machineCore = hashMap.get(List.of(machineBase));
                return Component.translatable("meeptech.moduleType." + machineBase).append(" ")
                    .append(Component.translatable("meeptech.moduleType." + machineCore));
            } else {
                return Component.translatable("item.meeptech.hull.partial").append(" ").append(super.getName(stack));
            }
        }
        return Component.translatable("item.meeptech.hull.empty").append(" ").append(super.getName(stack));
    }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
        MachineConfigData data = stack.get(ModDataComponents.MACHINE_CONFIG_DATA.get());
        HashMap<ArrayList<String>, String> hashMap = data.toHashMap();
        for (ArrayList<String> type : hashMap.keySet()) {
            hashMap.get(type);
            //TODO
        }
    }
}
