package com.spectrasonic.MangoUHC.listeners;

import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.enums.UHCState;
import com.spectrasonic.Utils.MessageUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import lombok.RequiredArgsConstructor;

/**
 * Listener para manejar el comportamiento del borde del mundo
 * Mata a los jugadores que salen del borde en lugar de permitir teletransporte
 */
@RequiredArgsConstructor
public class BorderListener implements Listener {

    private final Main plugin;

    /**
     * Maneja el movimiento de jugadores para verificar si están fuera del borde
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Solo verificar durante el UHC activo
        if (plugin.getUhcGameManager().getCurrentState() != UHCState.RUNNING) {
            return;
        }

        Player player = event.getPlayer();
        
        // Solo verificar jugadores en modo survival
        if (player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        Location to = event.getTo();
        if (to == null) {
            return;
        }

        World world = to.getWorld();
        if (world == null) {
            return;
        }

        WorldBorder border = world.getWorldBorder();
        
        // Verificar si el jugador está fuera del borde
        if (isOutsideBorder(to, border)) {
            // Matar al jugador directamente
            player.setHealth(0.0);
            
            // Mensaje personalizado de muerte por borde
            MessageUtils.sendBroadcastMessage("<red>" + player.getName() + " murió por salir del borde del mapa</red>");
            
            // Cancelar el movimiento para evitar que se complete
            event.setCancelled(true);
        }
    }

    /**
     * Verifica si una ubicación está fuera del borde del mundo
     */
    private boolean isOutsideBorder(Location location, WorldBorder border) {
        double borderSize = border.getSize();
        double borderRadius = borderSize / 2.0;
        
        Location center = border.getCenter();
        double centerX = center.getX();
        double centerZ = center.getZ();
        
        double playerX = location.getX();
        double playerZ = location.getZ();
        
        // Calcular la distancia desde el centro
        double distanceFromCenter = Math.sqrt(Math.pow(playerX - centerX, 2) + Math.pow(playerZ - centerZ, 2));
        
        // El jugador está fuera si la distancia es mayor que el radio
        return distanceFromCenter > borderRadius;
    }
}