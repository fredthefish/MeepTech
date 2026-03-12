package com.minecraftmod.meeptech.blocks;

import org.joml.Matrix4f;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.machine.MachineType;
import com.minecraftmod.meeptech.logic.ui.TrackedStat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class MachineBlockEntityRenderer implements BlockEntityRenderer<BaseMachineBlockEntity> {
    public MachineBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }
    @Override
    public void render(BaseMachineBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        MachineData data = entity.getMachineData();
        if (data == null) return;
        MachineType type = data.getType();
        String textureLocation = type.getId();
        boolean isOn = entity.getMachineInt(TrackedStat.HeatLeft) > 0;
        if (isOn) textureLocation += "_active";
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "textures/block/overlay/" + textureLocation + ".png");
        Direction facing = entity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        int light = LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos().relative(facing));
        if (isOn) light = LightTexture.FULL_BRIGHT;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        float angle = -facing.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(angle));
        poseStack.translate(0, 0, 0.501);
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(texture));
        Matrix4f matrix = poseStack.last().pose();
        drawVertex(builder, matrix, -0.5f, 0.5f, 0f, 0f, light, packedOverlay);
        drawVertex(builder, matrix, -0.5f, -0.5f, 0f, 1f, packedLight, packedOverlay);
        drawVertex(builder, matrix, 0.5f, -0.5f, 1f, 1f, packedLight, packedOverlay);
        drawVertex(builder, matrix, 0.5f, 0.5f, 1f, 0f, packedLight, packedOverlay);
        poseStack.popPose();
    }
    private void drawVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float u, float v, int packedLight, int packedOverlay) {
        builder.addVertex(matrix, x, y, 0).setColor(255, 255, 255, 255)
            .setUv(u, v).setOverlay(packedOverlay).setLight(packedLight).setNormal(0, 0, 1);
    }
}
