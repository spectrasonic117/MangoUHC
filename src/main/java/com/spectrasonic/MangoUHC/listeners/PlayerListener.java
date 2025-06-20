package com.spectrasonic.MangoUHC.listeners;

import com.spectrasonic.MangoUHC.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Listener para eventos relacionados con jugadores
 */
public class PlayerListener implements Listener {

    private final Main plugin;

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Muestra todos los timers activos a un jugador cuando se conecta
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getTimerManager().showAllTimersToPlayer(player);
    }
}
