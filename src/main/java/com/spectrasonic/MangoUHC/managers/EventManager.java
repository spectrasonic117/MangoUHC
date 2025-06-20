package com.spectrasonic.MangoUHC.managers;

import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.listeners.PlayerListener;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

/**
 * Manages event listener registration
 */
@RequiredArgsConstructor
public class EventManager {

    private final Main plugin;

    /**
     * Registers all event listeners
     */
    public void registerEvents() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        
        // Registrar el PlayerListener para manejar eventos de jugadores
        pluginManager.registerEvents(new PlayerListener(plugin), plugin);
    }
}
