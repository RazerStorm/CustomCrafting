package be.razerstorm.customcrafting.utils;

import org.bukkit.ChatColor;

public class ColorUtils {
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}