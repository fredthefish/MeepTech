package com.minecraftmod.meeptech.logic.ui;

public class RecipeUIModule extends UIModule {
    private boolean hasProgressBar;
    public RecipeUIModule(String title, boolean hasProgressBar) {
        super(UIModuleType.Recipe, title, 0);
        this.hasProgressBar = hasProgressBar;
    }
    public boolean hasProgressBar() {
        return hasProgressBar;
    }
}
