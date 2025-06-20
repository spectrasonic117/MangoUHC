package com.spectrasonic.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilidad para convertir códigos de color de Minecraft (&a, &b, etc.) a formato MiniMessage (<green>, <blue>, etc.)
 */
public class ColorConverter {

    private static final Map<String, String> COLOR_MAP = new HashMap<>();
    private static final Pattern COLOR_PATTERN = Pattern.compile("&([0-9a-fk-or])");

    static {
        // Colores básicos
        COLOR_MAP.put("&0", "<black>");
        COLOR_MAP.put("&1", "<dark_blue>");
        COLOR_MAP.put("&2", "<dark_green>");
        COLOR_MAP.put("&3", "<dark_aqua>");
        COLOR_MAP.put("&4", "<dark_red>");
        COLOR_MAP.put("&5", "<dark_purple>");
        COLOR_MAP.put("&6", "<gold>");
        COLOR_MAP.put("&7", "<gray>");
        COLOR_MAP.put("&8", "<dark_gray>");
        COLOR_MAP.put("&9", "<blue>");
        COLOR_MAP.put("&a", "<green>");
        COLOR_MAP.put("&b", "<aqua>");
        COLOR_MAP.put("&c", "<red>");
        COLOR_MAP.put("&d", "<light_purple>");
        COLOR_MAP.put("&e", "<yellow>");
        COLOR_MAP.put("&f", "<white>");
        
        // Formatos
        COLOR_MAP.put("&k", "<obfuscated>");
        COLOR_MAP.put("&l", "<bold>");
        COLOR_MAP.put("&m", "<strikethrough>");
        COLOR_MAP.put("&n", "<underlined>");
        COLOR_MAP.put("&o", "<italic>");
        COLOR_MAP.put("&r", "<reset>");
    }

    /**
     * Convierte códigos de color de Minecraft a formato MiniMessage
     * 
     * @param text Texto con códigos de color de Minecraft
     * @return Texto con formato MiniMessage
     */
    public static String convertToMiniMessage(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        Matcher matcher = COLOR_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String colorCode = matcher.group(0);
            String replacement = COLOR_MAP.getOrDefault(colorCode, colorCode);
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
