package com.minecraftmod.meeptech.blocks.pipes;

import com.minecraftmod.meeptech.MeepTech;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.event.ModelEvent;

public class PipeSprites {
    public static TextureAtlasSprite PIPE_END;
    public static TextureAtlasSprite PIPE_ARM;
    public static TextureAtlasSprite PIPE_ARM_INPUT;
    public static TextureAtlasSprite PIPE_ARM_OUTPUT;
    public static TextureAtlasSprite PIPE_END_INPUT;
    public static TextureAtlasSprite PIPE_END_OUTPUT;

    public static void init(ModelEvent.BakingCompleted event) {
        TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
        PIPE_END = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_end"));
        PIPE_ARM = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_arm"));
        PIPE_ARM_INPUT = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_arm_input"));
        PIPE_ARM_OUTPUT = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_arm_output"));
        PIPE_END_INPUT = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_end_input"));
        PIPE_END_OUTPUT = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_end_output"));
    }
}