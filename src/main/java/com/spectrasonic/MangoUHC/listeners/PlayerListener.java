package com.spectrasonic.MangoUHC.listeners;

import com.spectrasonic.MangoUHC.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
        plugin.getTimerManager().showAllTimersToPlayer(player);
    }
}
