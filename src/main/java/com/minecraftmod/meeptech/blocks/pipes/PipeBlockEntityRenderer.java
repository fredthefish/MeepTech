package com.minecraftmod.meeptech.blocks.pipes;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class PipeBlockEntityRenderer implements BlockEntityRenderer<PipeBlockEntity> {
    private static final float C_MIN = 6f / 16f;
    private static final float C_MAX = 10f / 16f;
    private static final float edgeMax = 1f;
    private static final float A_MIN = 6f / 16f;
    private static final float A_MAX = 10f / 16f;
    private static final float END_U = 4f / 16f;
    private static final float END_V = 4f / 16f;
    private static final float ARM_U = 6f / 16f;
    private static final float ARM_V = 4f / 16f;
    public PipeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}
    @Override
    public void render(PipeBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.cutout());
        renderCenterCube(be, state, pose, consumer, packedLight, packedOverlay);
        for (Direction dir : Direction.values()) {
            PipeConnection connection = state.getValue(PipeBlock.CONNECTIONS.get(dir));
            if (connection == PipeConnection.NONE) continue;
            renderArm(dir, connection, pose, consumer, packedLight, packedOverlay);
        }
    }
    private void renderQuad(PoseStack pose, VertexConsumer consumer, TextureAtlasSprite sprite, 
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            float nx, float ny, float nz,
            int packedLight, int packedOverlay, float u0, float v0, float u1, float v1) {
        Matrix4f matrix = pose.last().pose();
        u0 = sprite.getU(u0);
        v0 = sprite.getV(v0);
        u1 = sprite.getU(u1);
        v1 = sprite.getV(v1);
        consumer.addVertex(matrix, x1, y1, z1)
            .setColor(1f, 1f, 1f, 1f)
            .setUv(u0, v0)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x2, y2, z2)
            .setColor(1f, 1f, 1f, 1f)
            .setUv(u0, v1)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x3, y3, z3)
            .setColor(1f, 1f, 1f, 1f)
            .setUv(u1, v1)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(nx, ny, nz);
        consumer.addVertex(matrix, x4, y4, z4)
            .setColor(1f, 1f, 1f, 1f)
            .setUv(u1, v0)
            .setOverlay(packedOverlay)
            .setLight(packedLight)
            .setNormal(nx, ny, nz);
    }
    private void renderQuadBothSides(PoseStack pose, VertexConsumer consumer,
        TextureAtlasSprite sprite,
        float x1, float y1, float z1,
        float x2, float y2, float z2,
        float x3, float y3, float z3,
        float x4, float y4, float z4,
        float nx, float ny, float nz,
        int packedLight, int packedOverlay, float u0, float v0, float u1, float v1) {
        renderQuad(pose, consumer, sprite,
            x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4,
            nx, ny, nz, packedLight, packedOverlay, u0, v0, u1, v1);
        renderQuad(pose, consumer, sprite,
            x4, y4, z4, x3, y3, z3, x2, y2, z2, x1, y1, z1,
            -nx, -ny, -nz, packedLight, packedOverlay, u0, v0, u1, v1);
    }
    private void renderCenterCube(PipeBlockEntity be, BlockState state, PoseStack pose, VertexConsumer consumer, int packedLight, int packedOverlay) {
        for (Direction dir : Direction.values()) {
            PipeConnection connection = state.getValue(PipeBlock.CONNECTIONS.get(dir));
            TextureAtlasSprite sprite = connection == PipeConnection.NONE
                ? PipeSprites.ITEM_PIPE_END
                : null;
            if (sprite == null) continue;
            switch (dir) {
                case UP -> renderQuadBothSides(pose, consumer, sprite,
                    C_MIN, C_MAX, C_MAX,
                    C_MIN, C_MAX, C_MIN,
                    C_MAX, C_MAX, C_MIN,
                    C_MAX, C_MAX, C_MAX,
                    0, 1, 0, packedLight, packedOverlay, 0, 0, END_U, END_V);
                case DOWN -> renderQuadBothSides(pose, consumer, sprite,
                    C_MIN, C_MIN, C_MIN,
                    C_MIN, C_MIN, C_MAX,
                    C_MAX, C_MIN, C_MAX,
                    C_MAX, C_MIN, C_MIN,
                    0, -1, 0, packedLight, packedOverlay, 0, 0, END_U, END_V);
                case NORTH -> renderQuadBothSides(pose, consumer, sprite,
                    C_MAX, C_MAX, C_MIN,
                    C_MIN, C_MAX, C_MIN,
                    C_MIN, C_MIN, C_MIN,
                    C_MAX, C_MIN, C_MIN,
                    0, 0, -1, packedLight, packedOverlay, 0, 0, END_U, END_V);
                case SOUTH -> renderQuadBothSides(pose, consumer, sprite,
                    C_MIN, C_MAX, C_MAX,
                    C_MAX, C_MAX, C_MAX,
                    C_MAX, C_MIN, C_MAX,
                    C_MIN, C_MIN, C_MAX,
                    0, 0, 1, packedLight, packedOverlay, 0, 0, END_U, END_V);
                case WEST -> renderQuadBothSides(pose, consumer, sprite,
                    C_MIN, C_MAX, C_MIN,
                    C_MIN, C_MAX, C_MAX,
                    C_MIN, C_MIN, C_MAX,
                    C_MIN, C_MIN, C_MIN,
                    -1, 0, 0, packedLight, packedOverlay, 0, 0, END_U, END_V);
                case EAST -> renderQuadBothSides(pose, consumer, sprite,
                    C_MAX, C_MAX, C_MAX,
                    C_MAX, C_MAX, C_MIN,
                    C_MAX, C_MIN, C_MIN,
                    C_MAX, C_MIN, C_MAX,
                    1, 0, 0, packedLight, packedOverlay, 0, 0, END_U, END_V);
            }
        }
    }
    private void renderArm(Direction dir, PipeConnection connection, PoseStack pose, VertexConsumer consumer, int packedLight, int packedOverlay) {
        TextureAtlasSprite side = switch (connection) {
            case EXTRACTOR -> PipeSprites.ITEM_PIPE_ARM_INPUT;
            case INSERTER  -> PipeSprites.ITEM_PIPE_ARM_OUTPUT;
            default        -> PipeSprites.ITEM_PIPE_ARM;
        };
        TextureAtlasSprite end = switch (connection) {
            case EXTRACTOR -> PipeSprites.ITEM_PIPE_END_INPUT;
            case INSERTER  -> PipeSprites.ITEM_PIPE_END_OUTPUT;
            default        -> PipeSprites.ITEM_PIPE_END;
        };
        pose.pushPose();
        pose.translate(0.5, 0.5, 0.5);
        switch (dir) {
            case NORTH -> pose.mulPose(Axis.YP.rotationDegrees(180));
            case EAST  -> pose.mulPose(Axis.YP.rotationDegrees(90));
            case WEST  -> pose.mulPose(Axis.YP.rotationDegrees(-90));
            case UP    -> pose.mulPose(Axis.XP.rotationDegrees(-90));
            case DOWN  -> pose.mulPose(Axis.XP.rotationDegrees(90));
            case SOUTH -> {}
        }
        pose.translate(-0.5, -0.5, -0.5);
        renderQuadBothSides(pose, consumer, end,
            A_MIN, A_MAX, edgeMax,
            A_MIN, A_MIN, edgeMax,
            A_MAX, A_MIN, edgeMax,
            A_MAX, A_MAX, edgeMax,
            0f, 0f, -1f, packedLight, packedOverlay,
            0, 0, END_U, END_V);
        renderQuadBothSides(pose, consumer, side,
            A_MIN, A_MAX, C_MAX,
            A_MAX, A_MAX, C_MAX,
            A_MAX, A_MAX, edgeMax,
            A_MIN, A_MAX, edgeMax,
            0f, 1f, 0f, packedLight, packedOverlay,
            ARM_U, ARM_V, 0, 0);
        renderQuadBothSides(pose, consumer, side,
            A_MIN, A_MIN, edgeMax,
            A_MAX, A_MIN, edgeMax,
            A_MAX, A_MIN, C_MAX,
            A_MIN, A_MIN, C_MAX,
            0f, -1f, 0f, packedLight, packedOverlay,
            0, 0, ARM_U, ARM_V);
        renderQuadBothSides(pose, consumer, side,
            A_MIN, A_MAX, edgeMax,
            A_MIN, A_MIN, edgeMax,
            A_MIN, A_MIN, C_MAX,
            A_MIN, A_MAX, C_MAX,
            -1f, 0f, 0f, packedLight, packedOverlay,
            0, 0, ARM_U, ARM_V);
        renderQuadBothSides(pose, consumer, side,
            A_MAX, A_MAX, C_MAX,
            A_MAX, A_MIN, C_MAX,
            A_MAX, A_MIN, edgeMax,
            A_MAX, A_MAX, edgeMax,
            1f, 0f, 0f, packedLight, packedOverlay,
            ARM_U, ARM_V, 0, 0);
        pose.popPose();
    }
}