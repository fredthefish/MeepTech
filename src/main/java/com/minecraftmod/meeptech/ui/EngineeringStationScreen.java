package com.minecraftmod.meeptech.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.minecraftmod.meeptech.MeepTech;
import com.minecraftmod.meeptech.items.HullItem;
import com.minecraftmod.meeptech.logic.machine.MachineComponent;
import com.minecraftmod.meeptech.logic.machine.MachineConfigData;
import com.minecraftmod.meeptech.logic.material.ModMaterials;
import com.minecraftmod.meeptech.logic.module.ModuleType;
import com.minecraftmod.meeptech.network.EngineeringActionPacket;
import com.minecraftmod.meeptech.network.EngineeringActionPacket.EngineeringAction;
import com.minecraftmod.meeptech.registries.ModDataComponents;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class EngineeringStationScreen extends AbstractContainerScreen<EngineeringStationMenu> {
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "textures/gui/engineering_station.png");
    private static final ResourceLocation ADD_UPGRADE =
        ResourceLocation.fromNamespaceAndPath(MeepTech.MODID, "textures/gui/widgets/add_upgrade.png");
    // private static final ResourceLocation THUMB_TEXTURE = 
    //     ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/scroll_thumb.png");

    private final int boxX = 12;
    private final int boxY = 21;
    private final int slotSize = 18;
    private final int titleHeight = 12;
    private final int titleMargin = 2;

    private List<Integer> selectionPath = new ArrayList<>(0);
    
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
        
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        ItemStack editSlot = this.menu.getSlot(0).getItem();
        if (!editSlot.isEmpty()) {
            ModuleType type = ModuleType.getModuleType(editSlot);
            MachineConfigData data = (editSlot.has(ModDataComponents.MACHINE_CONFIG_DATA.get())) ? 
                editSlot.get(ModDataComponents.MACHINE_CONFIG_DATA.get()) : type.getEmptyMachineConfigData();
            int upgradeSlots = data.upgradeSlots();
            int layer = 0;
            int startX = x + boxX;
            int startY = y + boxY + titleHeight;
            
            while (selectionPath.size() >= layer) {
                if (type != null) for (int i = 0; i < type.getSubSlotCount(); i++) {
                    int slotX = startX + i * (slotSize + 1);
                    int slotY = startY + layer * (slotSize + titleHeight);
                    if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                        MachineConfigData subLayer = data.getSubLayer(i);
                        if (!subLayer.isEmpty()) {
                            ItemStack preview = new ItemStack(subLayer.getItem());
                            List<Component> tooltip = new ArrayList<>();
                            tooltip.add(preview.getHoverName());
                            if (selectionPath.size() > layer && selectionPath.get(layer) == i)
                                tooltip.add(Component.translatable("meeptech.ui.engineering_station.deselect"));
                            else tooltip.add(Component.translatable("meeptech.ui.engineering_station.select"));
                            if (!subLayer.hasSubLayers())
                                tooltip.add(Component.translatable("meeptech.ui.engineering_station.extract"));
                            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                        } else {
                            List<Component> tooltip = new ArrayList<>();
                            tooltip.add(Component.translatable(type.getSubSlot(i).getType().getTranslationKey()));
                            tooltip.add(Component.translatable("meeptech.ui.engineering_station.insert"));
                            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                        }
                    }
                    i++;
                }
                if (layer > 0) {
                    int i = 0;
                    for (i = 0; i < data.upgradeSlots(); i++) {
                        int slotX = startX + (type.getSubSlotCount() + i) * (slotSize + 1);
                        int slotY = startY + layer * (slotSize + titleHeight);
                        if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                            List<Component> tooltip = new ArrayList<>();
                            MachineConfigData subLayer = data.getSubLayer(type.getSubSlotCount() + i);
                            if (subLayer == null || subLayer.isEmpty()) {
                                tooltip.add(Component.translatable("meeptech.ui.engineering_station.insert_upgrade"));
                                tooltip.add(Component.translatable("meeptech.ui.engineering_station.remove_upgrade_slot"));
                            }
                            else tooltip.add(Component.translatable("meeptech.ui.engineering_station.extract_upgrade"));
                            tooltip.add(Component.translatable("meeptech.ui.engineering_station.upgrade_slots_left").append(Integer.toString(upgradeSlots)));
                            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                        }
                    }
                    if (upgradeSlots > 0) {
                        int slotX = startX + (type.getSubSlotCount() + i) * (slotSize + 1);
                        int slotY = startY + layer * (slotSize + titleHeight);
                        if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                            List<Component> tooltip = new ArrayList<>();
                            tooltip.add(Component.translatable("meeptech.ui.engineering_station.add_upgrade_slot"));
                            tooltip.add(Component.translatable("meeptech.ui.engineering_station.upgrade_slots_left").append(Integer.toString(upgradeSlots)));
                            guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                        }
                    }
                }
                if (selectionPath.size() > layer) {
                    data = data.getSubLayer(selectionPath.get(layer));
                    type = data.getModuleType();
                }
                layer++;
            }
        } else {
            if (!this.selectionPath.isEmpty()) selectionPath.clear();
        }
    }
    @Override
    public void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);

        ItemStack editSlot = this.menu.getSlot(0).getItem();
        if (!editSlot.isEmpty()) {
            Component titleText;
            if (editSlot.getItem() instanceof HullItem hullItem) titleText = hullItem.getTranslation(editSlot);
            else if (editSlot.getItem() instanceof BlockItem blockItem) titleText = Component.translatable(blockItem.getDescriptionId());
            else titleText = Component.translatable(editSlot.getItem().getDescriptionId());
            guiGraphics.drawString(this.font, titleText, x + boxX, y + boxY, 0xFFFFFF, false);
            ModuleType type = ModuleType.getModuleType(editSlot);
            MachineConfigData data = (editSlot.has(ModDataComponents.MACHINE_CONFIG_DATA.get())) ? 
                editSlot.get(ModDataComponents.MACHINE_CONFIG_DATA.get()) : type.getEmptyMachineConfigData();
            int upgradeSlots = data.upgradeSlots();
            int layer = 0;
            int startX = x + boxX;
            int startY = y + boxY + titleHeight;
    
            while (selectionPath.size() >= layer) {
                if (data.isComponent()) {
                    int stringY = startY + layer * (slotSize + titleHeight);
                    if (type.getAttribute() instanceof MachineComponent component) {
                        guiGraphics.drawString(this.font, component.getString(ModMaterials.getMaterial(data.materialId())), 
                            startX + 2, stringY, 0xFFFFFFFF, false);
                    }
                } else {
                    if (type != null) for (int i = 0; i < type.getSubSlotCount(); i++) {
                        int slotX = startX + i * (slotSize + 1);
                        int slotY = startY + layer * (slotSize + titleHeight);
                        int outlineColor = 0xFF373737;
                        if (selectionPath.size() > layer) if (selectionPath.get(layer) == i) outlineColor = 0xFFBBFFFF;
                        guiGraphics.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, outlineColor);
                        guiGraphics.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0xFF616161);
                        if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                            guiGraphics.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0x80FFFFFF); 
                        }
                        MachineConfigData subLayer = data.getSubLayer(i);
                        if (!subLayer.isEmpty()) {
                            ItemStack preview = new ItemStack(subLayer.getItem());
                            guiGraphics.renderItem(preview, slotX + 1, slotY + 1);
                        }
                    }
                    if (layer > 0) {
                        int i = 0;
                        for (i = 0; i < data.upgradeSlots(); i++) {
                            int slotX = startX + (type.getSubSlotCount() + i) * (slotSize + 1);
                            int slotY = startY + layer * (slotSize + titleHeight);
                            guiGraphics.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, 0xFF373737);
                            guiGraphics.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0xFF617161);
                            if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                                guiGraphics.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0x80FFFFFF); 
                            }
                            MachineConfigData subLayer = data.getSubLayer(type.getSubSlotCount() + i);
                            if (subLayer != null && !subLayer.isEmpty()) {
                                ItemStack preview = new ItemStack(subLayer.getItem());
                                guiGraphics.renderItem(preview, slotX + 1, slotY + 1);
                            }
                        }
                        if (upgradeSlots > 0) {
                            int slotX = startX + (type.getSubSlotCount() + i) * (slotSize + 1);
                            int slotY = startY + layer * (slotSize + titleHeight);
                            guiGraphics.fill(slotX, slotY, slotX + slotSize, slotY + slotSize, 0xFF373737);
                            guiGraphics.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0xFF616161);
                            if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                                guiGraphics.fill(slotX + 1, slotY + 1, slotX + slotSize - 1, slotY + slotSize - 1, 0x80FFFFFF); 
                            }
                            guiGraphics.blit(ADD_UPGRADE, slotX + 2, slotY + 2, 0, 0, 14, 14, 14, 14);
                        }
                    }
                }
                if (selectionPath.size() > layer) {
                    data = data.getSubLayer(selectionPath.get(layer));
                    type = data.getModuleType();
                    Item item = data.getItem();
                    if (item != null) {
                        ItemStack itemStack = new ItemStack(item);
                        if (!itemStack.isEmpty()) guiGraphics.drawString(this.font, itemStack.getHoverName(), 
                            startX, startY + (layer + 1) * (slotSize + titleHeight) - titleHeight + titleMargin, 0xFFFFFFFF, false);
                    }
                }
                layer++;
            }
        }
        guiGraphics.drawString(this.font, "Edit", x + 20, y + 152, 0x404040, false);
        guiGraphics.drawString(this.font, "In", x + 82, y + 152, 0x404040, false);
        guiGraphics.drawString(this.font, "Out", x + 130, y + 152, 0x404040, false);
    }
    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        ItemStack editSlot = this.menu.getSlot(0).getItem();
        if (!editSlot.isEmpty()) {
            ModuleType type = ModuleType.getModuleType(editSlot);
            MachineConfigData data = (editSlot.has(ModDataComponents.MACHINE_CONFIG_DATA.get())) ? 
                editSlot.get(ModDataComponents.MACHINE_CONFIG_DATA.get()) : type.getEmptyMachineConfigData();
            int upgradeSlots = data.upgradeSlots();
            int layer = 0;
            int startX = x + boxX;
            int startY = y + boxY + titleHeight;
    
            while (selectionPath.size() >= layer) {
                if (type != null) for (int i = 0; i < type.getSubSlotCount(); i++) {
                    int slotX = startX + i * (slotSize - 1);
                    int slotY = startY + layer * (slotSize + titleHeight);
                    if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                        MachineConfigData subLayer = data.getSubLayer(i);
                        if (button == 0) {
                            if (!subLayer.isEmpty()) {
                                List<Integer> newList = new ArrayList<>(selectionPath.subList(0, layer));
                                if (selectionPath.size() > layer) {
                                    if (selectionPath.get(layer) != i) newList.add(i);
                                } else newList.add(i);
                                selectionPath = newList;
                                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                            } else {
                                ItemStack input = this.menu.getSlot(1).getItem();
                                if (!input.isEmpty()) {
                                    if (input.getCount() >= editSlot.getCount() && 
                                    data.getModuleType().getSubSlot(i).getType().itemFitsSlotType(input)) {
                                        List<Integer> newList = new ArrayList<>(selectionPath.subList(0, layer));
                                        newList.add(i);
                                        PacketDistributor.sendToServer(new EngineeringActionPacket(EngineeringAction.INSERT, newList));
                                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                                    }
                                }
                            }
                        } else if (button == 1) {
                            if (!subLayer.isEmpty()) {
                                ItemStack output = this.menu.getSlot(2).getItem();
                                Item outputItem = subLayer.getItem();
                                if (!subLayer.hasSubLayers()) {
                                    if (output.isEmpty() 
                                    || (editSlot.getCount() <= output.getMaxStackSize() + 1 && output.getItem().equals(outputItem))) {
                                        List<Integer> newList = new ArrayList<>(selectionPath.subList(0, layer));
                                        newList.add(i);
                                        PacketDistributor.sendToServer(new EngineeringActionPacket(EngineeringAction.EXTRACT, newList));
                                        selectionPath = selectionPath.subList(0, layer);
                                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                                    }
                                }
                            }
                        }
                    }
                    i++;
                }
                if (layer > 0) {
                    int i = 0;
                    for (i = 0; i < data.upgradeSlots(); i++) {
                        int slotX = startX + type.getSubSlotCount() * (slotSize + 1);
                        int slotY = startY + layer * (slotSize + titleHeight);
                        if (mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                            if (button == 0) {
                                MachineConfigData subLayer = data.getSubLayer(type.getSubSlotCount() + i);
                                if (subLayer != null && !subLayer.isEmpty()) {
                                    List<Integer> newList = new ArrayList<>(selectionPath.subList(0, layer));
                                    int selection = type.getSubSlotCount() + i;
                                    if (selectionPath.size() > layer) {
                                        if (selectionPath.get(layer) != selection) newList.add(selection);
                                    } else newList.add(selection);
                                    selectionPath = newList;
                                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                                } else {
                                    ItemStack input = this.menu.getSlot(1).getItem();
                                    if (!input.isEmpty()) {
                                        if (input.getCount() >= editSlot.getCount() && 
                                        data.getModuleType().getUpgradeType().itemFitsSlotType(input)) {
                                            List<Integer> newList = new ArrayList<>(selectionPath.subList(0, layer));
                                            newList.add(i + type.getSubSlotCount());
                                            PacketDistributor.sendToServer(new EngineeringActionPacket(EngineeringAction.INSERT_UPGRADE, newList));
                                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                                        }
                                    }
                                }
                            } else if (button == 1) {
                                MachineConfigData subLayer = data.getSubLayer(type.getSubSlotCount() + i);
                                if (subLayer == null || subLayer.isEmpty()) {
                                    List<Integer> newList = new ArrayList<>(selectionPath.subList(0, layer));
                                    PacketDistributor.sendToServer(new EngineeringActionPacket(EngineeringAction.REMOVE_UPGRADE_SLOT, newList));
                                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                                } else {
                                    List<Integer> newList = new ArrayList<>(selectionPath.subList(0, layer));
                                    newList.add(i + type.getSubSlotCount());
                                    PacketDistributor.sendToServer(new EngineeringActionPacket(EngineeringAction.EXTRACT_UPGRADE, newList));
                                    selectionPath = selectionPath.subList(0, layer);
                                    this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                                }
                            }
                        }
                    }
                    if (upgradeSlots > 0) {
                        int slotX = startX + type.getSubSlotCount() * (slotSize + 1);
                        int slotY = startY + layer * (slotSize + titleHeight);
                        if (button == 0 && mouseX >= slotX && mouseX < slotX + slotSize && mouseY >= slotY && mouseY < slotY + slotSize) {
                            List<Integer> newList = new ArrayList<>(selectionPath.subList(0, layer));
                            PacketDistributor.sendToServer(new EngineeringActionPacket(EngineeringAction.ADD_UPGRADE_SLOT, newList));
                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                        }
                    }
                }
                if (selectionPath.size() > layer) {
                    data = data.getSubLayer(selectionPath.get(layer));
                    type = data.getModuleType();
                }
                layer++;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public void onClose() {
        selectionPath.clear();
        super.onClose();
    }
    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slot, slotId, mouseButton, type);
        if (slot != null && slot.index == 0) {
            this.selectionPath.clear();
        }
    }
}
