package com.minecraftmod.meeptech.ui;

import com.minecraftmod.meeptech.ModMachineTypes;
import com.minecraftmod.meeptech.logic.MachineType;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

public class DesigningStationScreen extends AbstractContainerScreen<DesigningStationMenu> {
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/designing_station.png");
    private int startIndex; //Which index is currently at the top.
    private final int listX = 43;
    private final int listY = 16;
    private final int listWidth = 115;
    private final int listHeight = 52;
    private final int itemHeight = 13;
    private final int visibleItems = 4;

    public DesigningStationScreen(DesigningStationMenu menu, Inventory inv, Component title) {
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

        int currentY = y + listY;
        for (int i = startIndex; (i < startIndex + visibleItems) && (i < ModMachineTypes.MACHINE_TYPES.size()); i++) {
            int color = (i == this.menu.getSelectedMachine()) ? 0xFFFFFF00 : 0xFFFFFFFF;
            if (mouseX >= x + listX && mouseX < x + listX + listWidth && mouseY >= currentY && mouseY < currentY + itemHeight) {
                guiGraphics.fill(x + listX, currentY, x + listX + listWidth, currentY + itemHeight, 0x50FFFFFF);
            }
            MachineType machine = ModMachineTypes.MACHINE_TYPES.get(i);
            guiGraphics.drawString(this.font, Component.translatable(machine.getTranslationKey()), x + listX + 1, currentY + 1, color, true);
            currentY += itemHeight;
        }
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int totalMachines = ModMachineTypes.MACHINE_TYPES.size();
        if (totalMachines > visibleItems) {
            int maxScroll = totalMachines - visibleItems;
            this.startIndex += (int)Math.signum(scrollY);
            this.startIndex = Math.clamp(this.startIndex, 0, maxScroll);
            return true;
        }
        return false;
    }
    @Override 
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (mouseX >= x + listX && mouseX < x + listX + listWidth) {
            int clickedY = (int)mouseY - (y + listY);
            if (clickedY >= 0 && clickedY < listHeight) {
                int clickedIndex = this.startIndex + (clickedY / itemHeight);
                if (clickedIndex >= 0 && clickedIndex < ModMachineTypes.MACHINE_TYPES.size()) {
                    this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, clickedIndex);
                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}