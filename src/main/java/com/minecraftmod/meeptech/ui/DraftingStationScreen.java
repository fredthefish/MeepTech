package com.minecraftmod.meeptech.ui;

import java.util.ArrayList;

import com.minecraftmod.meeptech.ModDataComponents;
import com.minecraftmod.meeptech.ModMachineComponents;
import com.minecraftmod.meeptech.ModMaterials;
import com.minecraftmod.meeptech.logic.BlueprintData;
import com.minecraftmod.meeptech.logic.ItemData;
import com.minecraftmod.meeptech.logic.MachineComponent;
import com.minecraftmod.meeptech.logic.MachineStat;
import com.minecraftmod.meeptech.logic.MachineType;
import com.minecraftmod.meeptech.logic.MaterialStat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class DraftingStationScreen extends AbstractContainerScreen<DraftingStationMenu> {
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/drafting_station.png");
    private static final ResourceLocation THUMB_TEXTURE = 
        ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/scroll_thumb.png");
    private int startIndex;
    private double boxScroll;
    private boolean isSaveButtonPressed = false;
    private final int listX = 43;
    private final int listY = 15;
    private final int statsY = 64;
    private final int boxWidth = 100;
    private final int boxHeight = 46;
    private final int scrollX = 146;
    private final int scrollHeight = 32;
    private final int scrollThumbWidth = 12;
    private final int scrollThumbHeight = 15;
    private final int itemHeight = 13;
    private final int visibleItems = 3;
    private final int buttonX = 16;
    private final int buttonY = 83;
    private final int buttonSize = 15;
    private final int padding = 4;

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
            if (this.isSaveButtonPressed) guiGraphics.fill(x + buttonX, y + buttonY, x + buttonX + buttonSize + 1, y + buttonY + buttonSize + 1, 0x60000000);
            else guiGraphics.fill(x + buttonX, y + buttonY, x + buttonX + buttonSize + 1, y + buttonY + buttonSize + 1, 0x50FFFFFF);
        }

        ItemStack blueprintStack = this.menu.getSlot(0).getItem();  

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
                    guiGraphics.drawWordWrap(this.font, Component.translatable(component.getTranslationKey()), x + listX + 1, currentY + 1, boxWidth, color);
                    currentY += itemHeight;
                }
                int selected = this.menu.getSelectedComponent();
                currentY = y + statsY;
                //Stats preview.
                if (selected >= 0 && selected < components.size()) {
                    ArrayList<Component> statsLines = getStatList();
                    int contentHeight = getContentHeight(statsLines);
                    if (contentHeight > boxHeight) {
                        guiGraphics.blit(THUMB_TEXTURE, x + scrollX, y + statsY + (int)((double)scrollHeight * this.boxScroll / (contentHeight - boxHeight)), 0, 0,
                            scrollThumbWidth, scrollThumbHeight, scrollThumbWidth, scrollThumbHeight);
                    } else {
                        this.boxScroll = 0;
                    }
                    guiGraphics.enableScissor(x + listX, currentY, x + listX + boxWidth, currentY + boxHeight);
                    currentY -= boxScroll;
                    for (Component line : statsLines) {
                        int lineHeight = this.font.wordWrapHeight(line, boxWidth);
                        if (currentY >= y + statsY || currentY <= y + statsY + boxHeight) {
                            guiGraphics.drawWordWrap(this.font, line, x + listX + 1, currentY + 1, boxWidth, 0xFF404040);
                        }
                        currentY += lineHeight + padding;
                    }
                    guiGraphics.disableScissor();
                }
            }
        }
    }
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, 114, 0x404040, false);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        if (mouseX >= x + scrollX && mouseX < x + scrollX + scrollThumbWidth && mouseY >= y + statsY && mouseY < y + statsY + boxHeight) {
            this.setDragging(true);
            return true;
        }
        if (mouseX >= x + buttonX && mouseX < x + buttonX + buttonSize && mouseY >= y + buttonY && mouseY < y + buttonY + buttonSize) {
            this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 1000);
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.isSaveButtonPressed = true;
            return true;
        }
        if (mouseX >= x + listX && mouseX < x + listX + boxWidth && mouseY >= y + listY && mouseY < y + listY + boxHeight) {
            ItemStack blueprintStack = this.menu.getSlot(0).getItem();
            if (blueprintStack.isEmpty()) return super.mouseClicked(mouseX, mouseY, button);
            BlueprintData data = blueprintStack.get(ModDataComponents.BLUEPRINT_DATA.get());
            MachineType type = data.getMachineType();
            if (type == null) return super.mouseClicked(mouseX, mouseY, button);
            ArrayList<MachineComponent> components = type.getComponents();
            int clickedY = (int)mouseY - (y + listY);
            int clickedIndex = clickedY / itemHeight;
            if (clickedIndex < components.size()) {
                this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, clickedIndex);
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.boxScroll = 0;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isDragging()) {
            int y = (this.height - this.imageHeight) / 2;
            double draggedY = mouseY - (y + statsY) - (scrollThumbHeight / 2);
            int contentHeight = getContentHeight(getStatList());
            if (contentHeight - boxHeight > 0) this.boxScroll = Math.clamp(draggedY / (double)scrollHeight * (double)(contentHeight - boxHeight),
                0, contentHeight - boxHeight);
            else this.boxScroll = 0;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.setDragging(false);
        this.isSaveButtonPressed = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        if (mouseX >= x + listX && mouseX < x + listX + boxWidth && mouseY >= y + listY && mouseY < y + listY + boxHeight) {
            ItemStack blueprintStack = this.menu.getSlot(0).getItem(); 
            if (!blueprintStack.isEmpty() && blueprintStack.has(ModDataComponents.BLUEPRINT_DATA.get())) {
                BlueprintData data = blueprintStack.get(ModDataComponents.BLUEPRINT_DATA.get());
                MachineType type = data.getMachineType();
                if (type != null) {
                    ArrayList<MachineComponent> components = type.getComponents();
                    if (components.size() > visibleItems) {
                        int maxScroll = components.size() - visibleItems;
                        this.startIndex += (int)Math.signum(scrollY);
                        this.startIndex = Math.clamp(this.startIndex, 0, maxScroll);
                        return true;
                    }
                }
            }
        }
        if (mouseX >= x + listX && mouseX < x + listX + boxWidth && mouseY >= y + statsY && mouseY < y + statsY + boxHeight) {
            ItemStack blueprintStack = this.menu.getSlot(0).getItem(); 
            if (!blueprintStack.isEmpty() && blueprintStack.has(ModDataComponents.BLUEPRINT_DATA.get())) {
                BlueprintData data = blueprintStack.get(ModDataComponents.BLUEPRINT_DATA.get());
                MachineType type = data.getMachineType();
                if (type != null) {
                    if (this.menu.getSelectedComponent() >= 0) {
                        int maxScroll = getContentHeight(getStatList());
                        this.boxScroll -= 5 * scrollY;
                        if (maxScroll - boxHeight > 0) this.boxScroll = Math.clamp(this.boxScroll, 0, maxScroll - boxHeight);
                        else this.boxScroll = 0;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public ArrayList<Component> getStatList() {
        ItemStack blueprintStack = this.menu.getSlot(0).getItem();  
        ItemStack materialStack = this.menu.getSlot(1).getItem();
        if (blueprintStack.isEmpty() || !blueprintStack.has(ModDataComponents.BLUEPRINT_DATA.get())) return null;
        BlueprintData data = blueprintStack.get(ModDataComponents.BLUEPRINT_DATA.get());
        MachineType type = data.getMachineType();
        if (type == null) return null;
        ArrayList<MachineComponent> components = type.getComponents();
        int selected = this.menu.getSelectedComponent();
        if (selected < 0) return null;

        ArrayList<Component> statsLines = new ArrayList<>();
        MachineComponent selectedComponent = components.get(selected);
        statsLines.add(Component.translatable("meeptech.ui.machine_component_cost")
            .append(Component.literal(Integer.toString(selectedComponent.getCost()) + " "))
            .append(Component.translatable(ModMaterials.FORM_TRANSLATION_KEYS.get(selectedComponent.getForm()))));
        statsLines.add(Component.translatable("meeptech.ui.machine_component_relevant_stat")
            .append(selectedComponent.getRelevantStatsString()));
        if (!materialStack.isEmpty()) {
            ItemData itemData = new ItemData(materialStack.getItem());
            if (itemData.getMaterial() != null) {
                statsLines.add(Component.translatable("meeptech.ui.inputted_material")
                    .append(Component.translatable(itemData.getMaterial().getTranslationKey())));
                MachineComponent machineComponent = components.get(selected);
                for (int i = 0; i < machineComponent.getRelevantStats().size(); i++) {
                    MaterialStat stat = machineComponent.getRelevantStats().get(i);
                    statsLines.add(Component.translatable(ModMaterials.MATERIAL_STAT_TRANSLATION_KEYS.get(stat))
                        .append(": ").append(itemData.getMaterial().getStatString(stat)));
                }
                for (int i = 0; i < machineComponent.getOutputStats().size(); i++) {
                    MachineStat stat = machineComponent.getOutputStats().get(i);
                    statsLines.add(Component.translatable(ModMachineComponents.MACHINE_STAT_TRANSLATION_KEYS.get(stat))
                        .append(": ").append(machineComponent.getStatString(stat, itemData.getMaterial())));
                }
            }
        }
        return statsLines;
    }
    public int getContentHeight(ArrayList<Component> lines) {
        int contentHeight = lines.size() > 0 ? padding * (lines.size() - 1) : 0;
        for (Component line : lines) {
            contentHeight += this.font.wordWrapHeight(line, boxWidth);
        }
        return contentHeight;
    }
}
