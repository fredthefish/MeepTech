package com.minecraftmod.meeptech.integration;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;
import com.minecraftmod.meeptech.helpers.Formatting;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.SimpleProgressStyle;

public enum MachineJadeProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof BaseMachineBlockEntity machine) {
            data.putInt("JadeHeat", machine.getMachineInt(TrackedStat.HeatLeft));
            data.putInt("JadeProgress", machine.getMachineInt(TrackedStat.RecipeProgress));
            data.putInt("JadeMaxProgress", machine.getMachineInt(TrackedStat.RecipeMaxProgress));
        }
    }
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof BaseMachineBlockEntity) {
            CompoundTag data = accessor.getServerData();
            IElementHelper helper = IElementHelper.get();

            if (data.getInt("JadeHeat") > 0) {
                tooltip.add(Component.literal("Heat: " + Formatting.doubleFormatting((double)data.getInt("JadeHeat") / 20.0) + "s"));
            }
            float progress = (float)data.getInt("JadeProgress");
            float maxProgress = (float)data.getInt("JadeMaxProgress");
            if (maxProgress > 0) {
                tooltip.add(helper.progress(progress / maxProgress, 
                    Component.literal(Formatting.doubleFormatting(progress / 20) + "s / " + Formatting.doubleFormatting(maxProgress / 20) + "s"),
                    new SimpleProgressStyle().color(0xFFFFFF).textColor(0xFFFFFFFF), BoxStyle.GradientBorder.DEFAULT_VIEW_GROUP, false));
            }
        }
    }
    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "machine_info");
    }
}
