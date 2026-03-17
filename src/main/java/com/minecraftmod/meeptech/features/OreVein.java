package com.minecraftmod.meeptech.features;

import java.util.ArrayList;
import java.util.List;

import com.minecraftmod.meeptech.logic.material.Material;

public class OreVein {
    private final String id;
    private final List<OreVeinEntry> entries;
    private int hRad;
    private int vRad;
    private float density;
    private int minY;
    private int maxY;
    public OreVein(String id) {
        this.id = id;
        this.entries = new ArrayList<>();
    }
    public record OreVeinEntry(Material material, float weight) {}
    public OreVein addOre(Material material, float weight) {
        entries.add(new OreVeinEntry(material, weight));
        return this;
    }
    public OreVein setData(int hRad, int vRad, float density, int minY, int maxY) {
        this.hRad = hRad;
        this.vRad = vRad;
        this.density = density;
        this.minY = minY;
        this.maxY = maxY;
        return this;
    }
    public String getId() {
        return id;
    }
    public List<OreVeinEntry> getEntries() {
        return entries;
    }
    public int getHorizontalRadius() {
        return hRad;
    }
    public int getVerticalRadius() {
        return vRad;
    }
    public float getDensity() {
        return density;
    }
    public int getMinY() {
        return minY;
    }
    public int getMaxY() {
        return maxY;
    }
}
