package dev.emir.models;

import org.bukkit.Material;

public class TrophyModel {

    private String name = "";
    private String description = "";
    private Material icon = Material.AIR;
    private long createdAt = 0;

    public TrophyModel(String name, String description, Material icon, long createdAt) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.createdAt = createdAt;
    }
}
