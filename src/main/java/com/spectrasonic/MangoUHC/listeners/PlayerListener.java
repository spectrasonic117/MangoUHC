package com.spectrasonic.MangoUHC.listeners;

import com.spectrasonic.MangoUHC.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import lombok.RequiredArgsConstructor;

/**
 * Listener para eventos relacionados con jugadores
 */
@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final Main plugin;

    /**
     * Muestra todos los timers activos a un jugador cuando se conecta
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Los timers persisten automaticamente, solo necesitamos mostrarlos al jugador
        plugin.getTimerManager().showAllTimersToPlayer(player);
    }

    /**
     * Maneja la desconexion de jugadores
     * Los timers continuan ejecutandose independientemente de las conexiones de jugadores
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Los timers ya persisten automaticamente en el TimerManager
        // No necesitamos hacer nada especial aqui ya que los BossBar se ocultan automaticamente
        // cuando el jugador se desconecta y se muestran nuevamente cuando se reconecta
    }
}