package com.spectrasonic.MangoUHC.listeners;

import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.enums.UHCState;
import com.spectrasonic.Utils.MessageUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
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

    /**
     * Maneja la muerte de jugadores durante el UHC
     * Cuando un jugador muere y el juego esta activo, lo cambia a modo espectador
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // Verificar si el UHC esta en estado RUNNING
        if (plugin.getUhcGameManager().getCurrentState() == UHCState.RUNNING) {
            // Cambiar al jugador a modo espectador
            player.setGameMode(GameMode.SPECTATOR);
            
            // Enviar mensaje al jugador
            MessageUtils.sendMessage(player, "<red>Has muerto y ahora eres un espectador. Puedes seguir observando el juego!");
            
            // Mensaje broadcast opcional para informar a otros jugadores
            String deathMessage = event.getDeathMessage();
            if (deathMessage != null && !deathMessage.isEmpty()) {
                MessageUtils.sendBroadcastMessage("<gray>" + deathMessage + " <yellow>- Ahora es espectador</yellow>");
                // Limpiar el mensaje de muerte original para evitar duplicados
                event.setDeathMessage("");
            } else {
                MessageUtils.sendBroadcastMessage("<gray>" + player.getName() + " ha muerto y ahora es espectador</gray>");
            }
            
            // Verificar condicion de victoria despues de cambiar al jugador a espectador
            // Usar un scheduler para asegurar que el cambio de gamemode se procese primero
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (plugin.getUhcGameManager().getWinConditionManager() != null) {
                    plugin.getUhcGameManager().getWinConditionManager().checkWinCondition();
                }
            }, 1L); // 1 tick de delay
        }
    }
}