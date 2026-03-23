package com.minecraftmod.meeptech.blocks.pipes;

import com.minecraftmod.meeptech.MeepTech;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.event.ModelEvent;

public class PipeSprites {
    public static TextureAtlasSprite ITEM_PIPE_END;
    public static TextureAtlasSprite ITEM_PIPE_ARM;
    public static TextureAtlasSprite ITEM_PIPE_ARM_INPUT;
    public static TextureAtlasSprite ITEM_PIPE_ARM_OUTPUT;
    public static TextureAtlasSprite ITEM_PIPE_END_INPUT;
    public static TextureAtlasSprite ITEM_PIPE_END_OUTPUT;

    public static void init(ModelEvent.BakingCompleted event) {
        TextureAtlas atlas = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
        ITEM_PIPE_END = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_end"));
        ITEM_PIPE_ARM = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_arm"));
        ITEM_PIPE_ARM_INPUT = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_arm_input"));
        ITEM_PIPE_ARM_OUTPUT = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_arm_output"));
        ITEM_PIPE_END_INPUT = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_end_input"));
        ITEM_PIPE_END_OUTPUT = atlas.getSprite(ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "block/pipe/pipe_end_output"));
    }
}