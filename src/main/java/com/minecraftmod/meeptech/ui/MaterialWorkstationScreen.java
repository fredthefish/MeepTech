package com.minecraftmod.meeptech.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.minecraftmod.meeptech.logic.MaterialWorkstationRecipe;
import com.minecraftmod.meeptech.logic.MaterialWorkstationRecipes;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MaterialWorkstationScreen extends AbstractContainerScreen<MaterialWorkstationMenu> {
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath("meeptech", "textures/gui/material_workstation.png");

    private static final int BUTTON_X_OFFSET = 42;
    private static final int BUTTON_Y_OFFSET = 15;
    private static final int BUTTON_SIZE = 18;
    private static final int COLUMNS = 5;

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

        ItemStack input = this.menu.getSlot(0).getItem();
        MaterialWorkstationRecipes recipes = MaterialWorkstationRecipes.getAvailableForms(input);
        if (recipes != null) {
            int x = (this.width - this.imageWidth) / 2;
            int y = (this.height - this.imageHeight) / 2;
            int startX = x + BUTTON_X_OFFSET;
            int startY = y + BUTTON_Y_OFFSET;
    
            for (int i = 0; i < recipes.getRecipes().size(); i++) {
                int buttonX = startX + (i % COLUMNS) * (BUTTON_SIZE - 1);
                int buttonY = startY + (i / COLUMNS) * (BUTTON_SIZE - 1);
                if (mouseX >= buttonX && mouseX < buttonX + BUTTON_SIZE && mouseY >= buttonY && mouseY < buttonY + BUTTON_SIZE) {
                    ItemStack outputItem = new ItemStack(recipes.getMaterial().getForm(recipes.getRecipes().get(i).getOutputForm()));
                    List<Component> tooltip = new ArrayList<>();
                    tooltip.add(outputItem.getHoverName());
                    tooltip.add(Component.literal("Cost: " + recipes.getRecipes().get(i).getInputAmount()).withStyle(ChatFormatting.GRAY));
                    guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
                }
            }
        }
    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 176, 166);

        ItemStack input = this.menu.getSlot(0).getItem();
        MaterialWorkstationRecipes recipes = MaterialWorkstationRecipes.getAvailableForms(input);
        if (recipes != null) {
            int startX = x + BUTTON_X_OFFSET;
            int startY = y + BUTTON_Y_OFFSET;
    
            int i = 0;
            for (MaterialWorkstationRecipe recipe : recipes.getRecipes()) {
                int buttonX = startX + (i % COLUMNS) * (BUTTON_SIZE - 1);
                int buttonY = startY + (i / COLUMNS) * (BUTTON_SIZE - 1);
                guiGraphics.fill(buttonX, buttonY, buttonX + BUTTON_SIZE, buttonY + BUTTON_SIZE, 0xFF373737);
                guiGraphics.fill(buttonX + 1, buttonY + 1, buttonX + BUTTON_SIZE - 1, buttonY + BUTTON_SIZE - 1, 0xFF616161);
                if (mouseX >= buttonX && mouseX < buttonX + BUTTON_SIZE && mouseY >= buttonY && mouseY < buttonY + BUTTON_SIZE) {
                    guiGraphics.fill(buttonX + 1, buttonY + 1, buttonX + BUTTON_SIZE - 1, buttonY + BUTTON_SIZE - 1, 0x80FFFFFF); 
                }
                ItemStack outputStack = new ItemStack(recipes.getMaterial().getForm(recipe.getOutputForm()), recipes.getRecipes().get(i).getOutputAmount());
                guiGraphics.renderItem(outputStack, buttonX + 1, buttonY + 1);
                guiGraphics.renderItemDecorations(this.font, outputStack, buttonX + 1, buttonY + 1);
                i++;
            }
        }
    }
    @Override 
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        ItemStack input = this.menu.getSlot(0).getItem();
        MaterialWorkstationRecipes recipes = MaterialWorkstationRecipes.getAvailableForms(input);
        if (recipes != null) {
            int startX = x + BUTTON_X_OFFSET;
            int startY = y + BUTTON_Y_OFFSET;
    
            for (int i = 0; i < recipes.getRecipes().size(); i++) {
                int buttonX = startX + (i % COLUMNS) * (BUTTON_SIZE - 1);
                int buttonY = startY + (i / COLUMNS) * (BUTTON_SIZE - 1);
                if (mouseX >= buttonX && mouseX < buttonX + BUTTON_SIZE && mouseY >= buttonY && mouseY < buttonY + BUTTON_SIZE) {
                    this.minecraft.gameMode.handleInventoryButtonClick((this.menu.containerId), 
                        Screen.hasShiftDown() ? i + 1000 : i); //Add 1000 to buttonId if shift is being held.
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}