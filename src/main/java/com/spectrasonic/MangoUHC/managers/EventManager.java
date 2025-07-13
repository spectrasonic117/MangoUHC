package com.spectrasonic.MangoUHC.managers;

import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.listeners.BlockListener;
import com.spectrasonic.MangoUHC.listeners.BorderListener;
import com.spectrasonic.MangoUHC.listeners.EntityListener;
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
        
        // Registrar el BlockListener para manejar eventos de bloques
        pluginManager.registerEvents(new BlockListener(plugin), plugin);
        
        // Registrar el EntityListener para manejar eventos de entidades
        pluginManager.registerEvents(new EntityListener(plugin), plugin);
        
        // Registrar el BorderListener para manejar eventos del borde del mundo
        pluginManager.registerEvents(new BorderListener(plugin), plugin);
    }
}
