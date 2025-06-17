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
}
