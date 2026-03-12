package com.minecraftmod.meeptech.ui;

import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.ui.RecipeUIModule;
import com.minecraftmod.meeptech.logic.ui.SlotUIElement;
import com.minecraftmod.meeptech.logic.ui.UIModule;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MachineScreen extends AbstractContainerScreen<MachineMenu> {
    private static final ResourceLocation BASE_UI = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/four_panel_ui.png");
    private static final ResourceLocation SLOT = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/slot.png");
    private static final ResourceLocation PROGRESS_BAR_EMPTY = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/progress_empty.png");
    private static final ResourceLocation PROGRESS_BAR_FULL = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/progress_full.png");
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
        for (UIModule uiModule : machineData.getUiModules()) {
            guiGraphics.drawString(this.font, uiModule.getTitle(), x + uiModule.getX() + 1, y + uiModule.getY() + 1, 0x404040, false);
            for (SlotUIElement slot : uiModule.getSlots()) {
                guiGraphics.blit(SLOT, x + slot.getX(), y + slot.getY(), 0, 0, slotSize, slotSize, slotSize, slotSize);
            }
            if (uiModule instanceof RecipeUIModule recipeUIModule) {
                if (recipeUIModule.hasProgressBar()) {
                    guiGraphics.blit(PROGRESS_BAR_EMPTY, x + recipeUIModule.getX() + 3, y + recipeUIModule.getY() + 13,
                        0, 0, 50, 13, 50, 13);
                    int progress = this.menu.getProgress();
                    int maxProgress = this.menu.getMaxProgress();
                    if (progress > 0 && maxProgress > 0) {
                        int fillWidth = (int)(50.0 * ((double)progress / (double)maxProgress));
                        guiGraphics.blit(PROGRESS_BAR_FULL, x + recipeUIModule.getX() + 3, y + recipeUIModule.getY() + 13,
                        0, 0, fillWidth, 13, 50, 13);
                    }
                }
            }
        }
    }
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
}