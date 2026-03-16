package com.minecraftmod.meeptech.logic.machine;

public class MachineStat {
    public static MachineStat SPEED = new MachineStat("speed");
    public static MachineStat EFFICIENCY = new MachineStat("efficiency");
    public static MachineStat HEAT_DECAY = new MachineStat("heat_decay");

    private String id;
    public MachineStat(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public String getTranslationKey() {
        return "meeptech.machineStat." + id;
    }
}