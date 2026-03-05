package com.minecraftmod.meeptech.ui;

import java.util.ArrayList;

import com.minecraftmod.meeptech.ModDataComponents;
import com.minecraftmod.meeptech.ModMaterials;
import com.minecraftmod.meeptech.logic.BlueprintData;
import com.minecraftmod.meeptech.logic.ItemData;
import com.minecraftmod.meeptech.logic.MachineComponent;
import com.minecraftmod.meeptech.logic.MachineType;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class DraftingStationScreen extends AbstractContainerScreen<DraftingStationMenu> {
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/drafting_station.png");
    private int startIndex;
    private final int listX = 43;
    private final int listY = 15;
    private final int statsY = 64;
    private final int boxWidth = 115;
    private final int boxHeight = 46;
    private final int itemHeight = 13;
    private final int visibleItems = 3;
    private final int buttonX = 16;
    private final int buttonY = 83;
    private final int buttonSize = 15;

    public DraftingStationScreen(DraftingStationMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
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
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        if (mouseX >= x + buttonX && mouseX < x + buttonX + buttonSize && mouseY >= y + buttonY && mouseY < y + buttonY + buttonSize) {
            guiGraphics.fill(x + buttonX, y + buttonY, x + buttonX + buttonSize, y + buttonY + buttonSize, 0x50FFFFFF);
        }

        ItemStack blueprintStack = this.menu.getSlot(0).getItem();  
        ItemStack materialStack = this.menu.getSlot(1).getItem();

        if (!blueprintStack.isEmpty() && blueprintStack.has(ModDataComponents.BLUEPRINT_DATA.get())) {
            BlueprintData data = blueprintStack.get(ModDataComponents.BLUEPRINT_DATA.get());
            MachineType type = data.getMachineType();
            if (type != null) {
                ArrayList<MachineComponent> components = type.getComponents();
                int currentY = y + listY;
                //Components list.
                for (int i = startIndex; (i < components.size() && (i < startIndex + visibleItems)); i++) {
                    int color = (i == this.menu.getSelectedComponent()) ? 0xFFFFFF00 : 0xFFFFFFFF;
                    if (mouseX >= x + listX && mouseX < x + listX + boxWidth && mouseY >= currentY && mouseY < currentY + itemHeight) {
                        guiGraphics.fill(x + listX, currentY, x + listX + boxWidth, currentY + itemHeight, 0x50FFFFFF);
                    }
                    MachineComponent component = components.get(i);
                    guiGraphics.drawString(this.font, Component.translatable(component.getTranslationKey()), x + listX + 1, currentY + 1, color, true);
                    currentY += itemHeight;
                }
                int selected = this.menu.getSelectedComponent();
                currentY = y + statsY;
                //Stats preview.
                if (selected >= 0 && selected < components.size()) {
                    MachineComponent selectedComponent = components.get(selected);
                    guiGraphics.drawString(this.font, Component.translatable(selectedComponent.getTranslationKey()), x + listX + 1, currentY + 1, 0xFF404040, false);
                    currentY += itemHeight;
                    guiGraphics.drawString(this.font, 
                        Component.translatable("meeptech.ui.machine_component_cost_tooltip")
                        .append(Component.literal(Integer.toString(selectedComponent.getCost()) + " "))
                        .append(Component.translatable(ModMaterials.FORM_TRANSLATION_KEYS.get(selectedComponent.getForm()))),
                        x + listX + 1, currentY + 1, 0xFF404040, false);
                    currentY += itemHeight;
                    guiGraphics.drawString(this.font, Component.translatable("meeptech.ui.machine_component_relevant_stat_tooltip")
                        .append(selectedComponent.getRelevantStatsString()),
                        x + listX + 1, currentY + 1, 0xFF404040, false);
                    if (!materialStack.isEmpty()) {
                        ItemData itemData = new ItemData(materialStack.getItem());
                        if (itemData.getMaterial() != null) {
                            currentY += itemHeight;
                            //TODO: Show material stat.
                        }
                    }
                }
            }
        }
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (mouseX >= x + buttonX && mouseX < x + buttonX + buttonSize && mouseY >= y + buttonY && mouseY < y + buttonY + buttonSize) {
            this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 1000);
            return true;
        }
        if (mouseX >= x + listX && mouseX < x + listX + boxWidth && mouseY >= y + listY && mouseY < y + listY + boxHeight) {
            int clickedY = (int)mouseY - (y + listY);
            int clickedIndex = clickedY / itemHeight;
            this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, clickedIndex);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
