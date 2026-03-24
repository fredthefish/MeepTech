package com.minecraftmod.meeptech.ui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidTankWidget {
    private final int x, y, width, height;
    private final int tankIndex;
    private static final ResourceLocation FLUID_SLOT = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/widgets/fluid_slot.png");

    public FluidTankWidget(int x, int y, int width, int height, int tankIndex) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.tankIndex = tankIndex;
    }
    public void render(GuiGraphics graphics, MachineMenu menu) {
        FluidTank tank = menu.getTank(tankIndex);
        FluidStack fluid = tank.getFluid();
        int capacity = tank.getCapacity();
        graphics.blit(FLUID_SLOT, x, y, width, height, 0, 0, 18, 18, 18, 18);
        if (!fluid.isEmpty() && capacity > 0) {
            renderFluidStack(graphics, fluid, x, y);
        }
    }
    public void renderOverlay(GuiGraphics graphics) {
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, 0x80FFFFFF); 
    }
    private void renderFluidStack(GuiGraphics graphics, FluidStack fluid, int x, int y) {
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluid.getFluid());
        ResourceLocation stillTexture = props.getStillTexture(fluid);
        if (stillTexture == null) return;
        int tintColor = props.getTintColor(fluid);
        float a = ((tintColor >> 24) & 0xFF) / 255f;
        float r = ((tintColor >> 16) & 0xFF) / 255f;
        float g = ((tintColor >> 8) & 0xFF) / 255f;
        float b = (tintColor & 0xFF) / 255f;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.setShaderColor(r, g, b, a);
        int spriteSize = 16;
        graphics.blit(x + 1, y + 1, 0, spriteSize, spriteSize, sprite);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
    public boolean isMouseOver(double mouseX, double mouseY) {
        int renderX = x;
        int renderY = y;
        return mouseX >= renderX && mouseX < renderX + width && mouseY >= renderY && mouseY < renderY + height;
    }
    public List<Component> getTooltip(MachineMenu menu) {
        FluidTank tank = menu.getTank(tankIndex);
        FluidStack fluid = tank.getFluid();
        int capacity = tank.getCapacity();
        List<Component> tooltip = new ArrayList<>();
        if (fluid.isEmpty()) {
            tooltip.add(Component.translatable("meeptech.ui.empty_tank"));
            tooltip.add(Component.literal(capacity + " mB").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(fluid.getHoverName());
            tooltip.add(Component.literal(fluid.getAmount() + " / " + capacity + " mB").withStyle(ChatFormatting.GRAY));
        }
        return tooltip;
    }
}