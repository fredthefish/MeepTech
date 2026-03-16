package com.minecraftmod.meeptech.items;

import org.joml.Matrix4f;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.logic.machine.MachineConfigData;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.registries.ModDataComponents;
import com.minecraftmod.meeptech.registries.ModTags;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class MachineItemRenderer extends BlockEntityWithoutLevelRenderer {
    public MachineItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        BlockState hullState = null;
        if (stack.is(ModTags.HULL_TAG)) {
            hullState = ((HullItem)stack.getItem()).getBlock().defaultBlockState();
        }
        blockRenderer.renderSingleBlock(hullState, poseStack, buffer, packedLight, packedOverlay, ModelData.EMPTY, null);
        MachineConfigData config = stack.getOrDefault(ModDataComponents.MACHINE_CONFIG_DATA.get(), MachineConfigData.EMPTY);
        MachineData data = config.toMachineData();
        if (data != null) {
            String textureName = data.getType().getId();
            ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "textures/block/overlay/" + textureName + ".png");
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.translate(0, 0, 0.501);
            VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(texture));
            Matrix4f matrix = poseStack.last().pose();
            drawVertex(builder, matrix, -0.5f,  0.5f, 0.0f, 0.0f, packedLight, packedOverlay);
            drawVertex(builder, matrix, -0.5f, -0.5f, 0.0f, 1.0f, packedLight, packedOverlay);
            drawVertex(builder, matrix,  0.5f, -0.5f, 1.0f, 1.0f, packedLight, packedOverlay);
            drawVertex(builder, matrix,  0.5f,  0.5f, 1.0f, 0.0f, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }
    private void drawVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float u, float v, int light, int overlay) {
        builder.addVertex(matrix, x, y, 0).setColor(255, 255, 255, 255).setUv(u, v).setOverlay(overlay)
        .setLight(light).setNormal(0, 0, 1);
    }
}
