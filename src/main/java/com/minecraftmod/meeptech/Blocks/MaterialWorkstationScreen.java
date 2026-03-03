package com.minecraftmod.meeptech.Blocks;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MaterialWorkstationScreen extends AbstractContainerScreen<MaterialWorkstationMenu> {
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/material_workstation.png");

    public MaterialWorkstationScreen(MaterialWorkstationMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 176, 166);
        // int selected = this.menu.getSelectedForm();
        // guiGraphics.drawString(this.font, "Selected Form: " + selected, x + 65, y + 15, 0x404040, false);
    }
    @Override 
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (mouseX >= x + 65 && mouseX <= x + 85 && mouseY >= y + 30 && mouseY <= y + 50) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}