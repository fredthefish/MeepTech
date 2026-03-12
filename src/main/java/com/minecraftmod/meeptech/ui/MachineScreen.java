package com.minecraftmod.meeptech.ui;

import com.minecraftmod.meeptech.helpers.Formatting;
import com.minecraftmod.meeptech.logic.machine.MachineData;
import com.minecraftmod.meeptech.logic.ui.EnergyUIModule;
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
    private static final ResourceLocation SLOT = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/widgets/slot.png");
    private static final ResourceLocation PROGRESS_BAR_EMPTY = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/widgets/progress_empty.png");
    private static final ResourceLocation PROGRESS_BAR_FULL = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/widgets/progress_full.png");
    private static final ResourceLocation HEAT_OFF = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/widgets/heat_off.png");
    private static final ResourceLocation HEAT_ON = ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/widgets/heat_on.png");
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
                        0, 0, 49, 13, 49, 13);
                    int progress = this.menu.getProgress();
                    int maxProgress = this.menu.getMaxProgress();
                    if (maxProgress > 0) {
                        int fillWidth = (int)(49.0 * ((double)progress / (double)maxProgress));
                        guiGraphics.blit(PROGRESS_BAR_FULL, x + recipeUIModule.getX() + 3, y + recipeUIModule.getY() + 13,
                        0, 0, fillWidth, 13, 49, 13);
                        String progressString = Formatting.doubleSigFigFormat((double)progress / 20.0, 3, 2) + "s / " + 
                            Formatting.doubleSigFigFormat((double)maxProgress / 20.0, 3, 2) + "s";
                        guiGraphics.drawString(font, progressString, x + recipeUIModule.getX() + 3, y + recipeUIModule.getY() + 32,
                            0x404040, false);
                    }
                }
            } else if (uiModule instanceof EnergyUIModule energyUIModule) {
                if (energyUIModule.hasHeatIcon()) {
                    guiGraphics.blit(HEAT_OFF, x + energyUIModule.getX() + 5, y + energyUIModule.getY() + 33,
                        0, 0, 13, 13, 13, 13);
                    int heat = this.menu.getHeat();
                    if (this.menu.getHeat() > 0) {
                        guiGraphics.blit(HEAT_ON, x + energyUIModule.getX() + 4, y + energyUIModule.getY() + 32,
                        0, 0, 13, 13, 13, 13);
                        guiGraphics.drawString(font, Formatting.doubleSigFigFormat((double)heat / 20.0, 3, 2) + "s",
                            x + energyUIModule.getX() + 25, y + energyUIModule.getY() + 15, 0x404040, false);
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