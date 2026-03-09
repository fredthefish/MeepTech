package com.minecraftmod.meeptech.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.minecraftmod.meeptech.ModDataComponents;
import com.minecraftmod.meeptech.ModModuleTypes;
import com.minecraftmod.meeptech.items.MachineConfigData;
import com.minecraftmod.meeptech.logic.ModuleType;
import com.minecraftmod.meeptech.network.EngineeringActionPacket;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class EngineeringStationScreen extends AbstractContainerScreen<EngineeringStationMenu> {
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/engineering_station.png");
    // private static final ResourceLocation THUMB_TEXTURE = 
    //     ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/scroll_thumb.png");

    private final int boxX = 12;
    private final int boxY = 21;
    private final int slotSize = 18;
    private final int titleHeight = 12;
    
    public EngineeringStationScreen(EngineeringStationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 200;
        this.imageHeight = 256;
    }
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY); 
        
        ItemStack hull = this.menu.getSlot(0).getItem();
        if (!hull.isEmpty()) {
            int x = (this.width - this.imageWidth) / 2;
            int y = (this.height - this.imageHeight) / 2;
            MachineConfigData data;
            ModuleType type = ModModuleTypes.BASE_MODULE;
            if (!hull.has(ModDataComponents.MACHINE_CONFIG_DATA.get())) {
                data = type.getEmptyMachineConfigData();
            } else {
                data = hull.get(ModDataComponents.MACHINE_CONFIG_DATA.get());
            }
            int startX = x + boxX;
            int startY = y + boxY + titleHeight;
    
            for (int i = 0; i < type.getSubSlotCount(); i++) {
                int slotX = startX + i * (slotSize - 1);
                int slotY = startY;
                if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                    MachineConfigData subLayer = data.getSubLayer(i);
                    if (!subLayer.isEmpty()) {
                        ItemStack preview = new ItemStack(subLayer.getModuleType().getItem());
                        List<Component> tooltip = new ArrayList<>();
                        tooltip.add(preview.getHoverName());
                        guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                    } else {
                        List<Component> tooltip = new ArrayList<>();
                        tooltip.add(Component.translatable(ModModuleTypes.MODULE_SLOT_TRANSLATION_KEYS.get(type.getSubSlot(i).getType())));
                        guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                    }
                }
                i++;
            }
        }
    }
    @Override
    public void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        ItemStack hull = this.menu.getSlot(0).getItem();
        if (!hull.isEmpty()) {
            guiGraphics.drawString(this.font, hull.getHoverName(), x + boxX, y + boxY, 0x404040);
            ModuleType type = ModModuleTypes.BASE_MODULE;
            MachineConfigData data;
            if (!hull.has(ModDataComponents.MACHINE_CONFIG_DATA.get())) {
                data = type.getEmptyMachineConfigData();
            } else {
                data = hull.get(ModDataComponents.MACHINE_CONFIG_DATA.get());
            }
            int startX = x + boxX;
            int startY = y + boxY + titleHeight;
    
            for (int i = 0; i < type.getSubSlotCount(); i++) {
                int slotX = startX + i * (slotSize - 1);
                int slotY = startY;
                guiGraphics.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, 0xFF373737);
                guiGraphics.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0xFF616161);
                if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                    guiGraphics.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0x80FFFFFF); 
                }
                MachineConfigData subLayer = data.getSubLayer(i);
                if (!subLayer.isEmpty()) {
                    ItemStack preview = new ItemStack(subLayer.getModuleType().getItem());
                    guiGraphics.renderItem(preview, slotX + 1, slotY + 1);
                }
                i++;
            }
        }

    }
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        ItemStack hull = this.menu.getSlot(0).getItem();
        if (!hull.isEmpty()) {
            ModuleType type = ModModuleTypes.BASE_MODULE;
            MachineConfigData data;
            if (!hull.has(ModDataComponents.MACHINE_CONFIG_DATA.get())) {
                data = type.getEmptyMachineConfigData();
            } else {
                data = hull.get(ModDataComponents.MACHINE_CONFIG_DATA.get());
            }
            int startX = x + boxX;
            int startY = y + boxY + titleHeight;
    
            for (int i = 0; i < type.getSubSlotCount(); i++) {
                int slotX = startX + i * (slotSize - 1);
                int slotY = startY;
                if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                    MachineConfigData subLayer = data.getSubLayer(i);
                    if (button == 0) {
                        if (!subLayer.isEmpty()) {
                            PacketDistributor.sendToServer(new EngineeringActionPacket(EngineeringActionPacket.EngineeringAction.SELECT, List.of(i)));
                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                        } else {
                            ItemStack input = this.menu.getSlot(1).getItem();
                            if (!input.isEmpty()) {
                                if (ModuleType.itemFitsSlotType(input, data.getModuleType().getSubSlot(i).getType())) {
                                    PacketDistributor.sendToServer(new EngineeringActionPacket(EngineeringActionPacket.EngineeringAction.INSERT, List.of(i)));
                                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                                }
                            }
                        }
                    } else if (button == 1) {
                        //Right Click.
                    }
                }
                i++;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
