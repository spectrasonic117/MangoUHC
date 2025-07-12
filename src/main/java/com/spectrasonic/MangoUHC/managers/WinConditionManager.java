package com.spectrasonic.MangoUHC.managers;

import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.enums.UHCState;
import com.spectrasonic.Utils.MessageUtils;
import com.spectrasonic.Utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maneja las condiciones de victoria del UHC FFA
 */
@RequiredArgsConstructor
public class WinConditionManager {

    private final Main plugin;
    private boolean gameEnded = false;

    /**
     * Verifica si hay un ganador después de que un jugador muere
     * Debe ser llamado después de cambiar al jugador muerto a espectador
     */
    public void checkWinCondition() {
        // Solo verificar si el juego está activo y no ha terminado ya
        if (plugin.getUhcGameManager().getCurrentState() != UHCState.RUNNING || gameEnded) {
            return;
        }

        List<Player> alivePlayers = getAlivePlayers();
        
        // Si solo queda 1 jugador vivo, declarar ganador
        if (alivePlayers.size() == 1) {
            Player winner = alivePlayers.get(0);
            declareWinner(winner);
        } else if (alivePlayers.isEmpty()) {
            // Caso edge: si no hay jugadores vivos (empate o error)
            declareNoWinner();
        }
    }

    /**
     * Obtiene la lista de jugadores vivos (en modo survival)
     */
    private List<Player> getAlivePlayers() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                .collect(Collectors.toList());
    }

    /**
     * Declara al ganador del UHC
     */
    private void declareWinner(Player winner) {
        gameEnded = true;
        
        // Enviar title a todos los jugadores
        MessageUtils.broadcastTitle(
            "<gold><b>GANADOR</b></gold>", 
            "<white><b>" + winner.getName() + "</b></white>", 
            1, 5, 2
        );
        
        // Mensaje en chat
        MessageUtils.sendBroadcastMessage("<gold>¡" + winner.getName() + " ha ganado el UHC!</gold>");
        
        // Sonido de victoria
        SoundUtils.broadcastPlayerSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        
        // Programar el final del juego después de unos segundos para que los jugadores vean el title
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getUhcGameManager().endUHCWithWinner(winner);
        }, 20L * 10); // 10 segundos después
    }

    /**
     * Maneja el caso donde no hay ganador (empate)
     */
    private void declareNoWinner() {
        gameEnded = true;
        
        // Enviar title de empate
        MessageUtils.broadcastTitle(
            "<red><b>EMPATE</b></red>", 
            "<white>No hay ganador</white>", 
            1, 5, 2
        );
        
        // Mensaje en chat
        MessageUtils.sendBroadcastMessage("<red>¡El UHC ha terminado sin ganador!</red>");
        
        // Sonido de final
        SoundUtils.broadcastPlayerSound(Sound.ENTITY_WITHER_DEATH, 1.0f, 0.8f);
        
        // Programar el final del juego
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getUhcGameManager().endUHCWithoutWinner();
        }, 20L * 10); // 10 segundos después
    }

    /**
     * Reinicia el estado del manager para un nuevo juego
     */
    public void reset() {
        gameEnded = false;
    }

    /**
     * Verifica si el juego ya terminó
     */
    public boolean hasGameEnded() {
        return gameEnded;
    }

    /**
     * Obtiene el número de jugadores vivos actualmente
     */
    public int getAlivePlayersCount() {
        return getAlivePlayers().size();
    }
}