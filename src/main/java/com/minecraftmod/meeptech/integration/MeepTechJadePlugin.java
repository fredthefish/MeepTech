package com.minecraftmod.meeptech.integration;

import com.minecraftmod.meeptech.blocks.BaseMachineBlock;
import com.minecraftmod.meeptech.blocks.BaseMachineBlockEntity;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MeepTechJadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(MachineJadeProvider.INSTANCE, BaseMachineBlockEntity.class);
    }
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(MachineJadeProvider.INSTANCE, BaseMachineBlock.class);
    }
}
