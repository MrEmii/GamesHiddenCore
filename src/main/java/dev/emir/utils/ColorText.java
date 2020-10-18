package dev.emir.utils;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class ColorText {

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> translate(String... messages) {
        List<String> toReturn = new ArrayList<>();
        for (String message : messages) {
            toReturn.add(translate(message));
        }
        return toReturn;
    }

    public static List<String> translate(List<String> messages) {
        List<String> toReturn = new ArrayList<>();
        for (String message : messages) {
            toReturn.add(translate(message));
        }
        return toReturn;
    }

    public static String resetColor(String color) {
        return ChatColor.stripColor(color);
    }
}