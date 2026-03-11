package com.minecraftmod.meeptech.ui;

import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.ui.SlotUIElement;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MachineScreen extends AbstractContainerScreen<MachineMenu> {
    private static final ResourceLocation BASE_UI = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/four_panel_ui.png");
    private static final ResourceLocation SLOT = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/slot.png");
    private static final int slotSize = 18;
    public MachineScreen(MachineMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = 176;
        this.imageHeight = 206;
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

        guiGraphics.blit(BASE_UI, x, y, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        MachineData machineData = this.menu.getMachineData();
        for (SlotUIElement slot : machineData.getSlots()) {
            guiGraphics.blit(SLOT, x + slot.getX() - 1, y + slot.getY() - 1, 0, 0, slotSize, slotSize, slotSize, slotSize);
        }
    }
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
}