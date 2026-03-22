package com.minecraftmod.meeptech.ui;

import com.minecraftmod.meeptech.MeepTech;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidTankScreen extends AbstractContainerScreen<FluidTankMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "textures/gui/blank_ui.png");
    private static final ResourceLocation FILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "textures/gui/widgets/tank_fill.png");

    private static final int GUI_WIDTH = 176;
    private static final int GUI_HEIGHT = 166;
    private static final int FLUID_BAR_X = 80;
    private static final int FLUID_BAR_Y = 16;
    private static final int FLUID_BAR_WIDTH = 16;
    private static final int FLUID_BAR_HEIGHT = 52;

    public FluidTankScreen(FluidTankMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        graphics.blit(FILL_TEXTURE, leftPos + FLUID_BAR_X - 1, topPos + FLUID_BAR_Y - 1, 0, 0, 
            FLUID_BAR_WIDTH + 2, FLUID_BAR_HEIGHT + 2, FLUID_BAR_WIDTH + 2, FLUID_BAR_HEIGHT + 2);
        FluidStack fluid = menu.getFluid();
        int amount = menu.getFluidAmount();
        int capacity = menu.getCapacity();
        if (!fluid.isEmpty() && capacity > 0) {
            int remaining = (int)((float) amount / capacity * FLUID_BAR_HEIGHT);
            int spriteHeight = 16;
            int barX = leftPos + FLUID_BAR_X;
            int barBottom = topPos + FLUID_BAR_Y + FLUID_BAR_HEIGHT;
            TextureAtlasSprite sprite = getFluidSprite(fluid);
            if (sprite != null) {
                int color = getFluidColor(fluid);
                float r = ((color >> 16) & 0xFF) / 255f;
                float g = ((color >> 8) & 0xFF) / 255f;
                float b = (color & 0xFF) / 255f;
                float a = ((color >> 24) & 0xFF) / 255f;
                if (a == 0) a = 1f;
                while (remaining > 0) {
                    int segmentHeight = Math.min(remaining, spriteHeight);
                    int segmentY = barBottom - remaining;
                    graphics.blit(barX, segmentY, 0, FLUID_BAR_WIDTH, segmentHeight, sprite, r, g, b, a);
                    remaining -= segmentHeight;
                }
            }
        }
    }
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
        String fluidText = menu.getFluidAmount() + " / " + menu.getCapacity() + " mB";
        int textX = FLUID_BAR_X + FLUID_BAR_WIDTH / 2 - font.width(fluidText) / 2;
        graphics.drawString(font, fluidText, textX, FLUID_BAR_Y + FLUID_BAR_HEIGHT + 4, 0x404040, false);
        if (mouseX >= leftPos + FLUID_BAR_X && mouseX <= leftPos + FLUID_BAR_X + FLUID_BAR_WIDTH 
        && mouseY >= topPos + FLUID_BAR_Y && mouseY <= topPos + FLUID_BAR_Y + FLUID_BAR_HEIGHT) {
            FluidStack fluid = menu.getFluid();
            if (!fluid.isEmpty()) {
                graphics.renderTooltip(font, Component.translatable(fluid.getFluid().getFluidType().getDescriptionId()), mouseX - leftPos, mouseY - topPos);
            }
        }
    }
    private TextureAtlasSprite getFluidSprite(FluidStack fluid) {
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType());
        ResourceLocation stillTexture = extensions.getStillTexture(fluid);
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
    }
    private int getFluidColor(FluidStack fluid) {
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType());
        return extensions.getTintColor(fluid);
    }
}