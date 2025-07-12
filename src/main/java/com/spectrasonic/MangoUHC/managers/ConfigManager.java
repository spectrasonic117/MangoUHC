package com.spectrasonic.MangoUHC.managers;

import com.spectrasonic.MangoUHC.Main;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final Main plugin;
    @Getter
    private FileConfiguration config;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public void saveConfig() {
        plugin.saveConfig();
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    /**
     * Obtiene el radio del borde donde se colocaran los jugadores al inicio del UHC
     * @return radio en bloques
     */
    public int getBorderRadius() {
        return config.getInt("uhc.border-radius", 1000);
    }

    /**
     * Obtiene el tamano del world border
     * @return tamano del world border
     */
    public double getWorldBorderSize() {
        return config.getDouble("uhc.world-border.size", 3000);
    }

    /**
     * Obtiene el numero maximo de intentos para encontrar una ubicacion segura
     * @return numero maximo de intentos
     */
    public int getScatterMaxAttempts() {
        return config.getInt("uhc.scatter.max-attempts", 50);
    }
}