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

    /**
     * Obtiene el estado del PvP desde la configuracion
     * @return true si el PvP esta habilitado, false en caso contrario
     */
    public boolean isPvPEnabled() {
        return config.getBoolean("uhc.pvp.enabled", false);
    }

    /**
     * Establece el estado del PvP en la configuracion
     * @param enabled true para habilitar PvP, false para deshabilitar
     */
    public void setPvPEnabled(boolean enabled) {
        config.set("uhc.pvp.enabled", enabled);
        saveConfig();
    }

    /**
     * Obtiene la probabilidad de drop de manzanas desde las hojas
     * @return probabilidad entre 0.0 y 1.0
     */
    public double getAppleDropChance() {
        return config.getDouble("uhc.drops.apple-drop-chance", 0.25);
    }
}