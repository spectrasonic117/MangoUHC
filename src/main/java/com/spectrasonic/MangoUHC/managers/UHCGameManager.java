package com.spectrasonic.MangoUHC.managers;

import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.enums.UHCState;
import com.spectrasonic.Utils.MessageUtils;
import com.spectrasonic.Utils.SoundUtils;
import org.bukkit.Sound;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.GameMode;

import java.util.Collection;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.NonNull;

@Getter
@RequiredArgsConstructor
public class UHCGameManager {

    private final Main plugin;
    private final ConfigManager configManager;
    @Setter private UHCTimerManager uhcTimerManager;
    private final WorldBorderManager worldBorderManager = new WorldBorderManager();
    private WinConditionManager winConditionManager;
    private UHCState currentState = UHCState.STOPPED;
    private final Random random = new Random();
    
    public WorldBorderManager getWorldBorderManager() {
        return worldBorderManager;
    }

    /**
     * Inicializa el WinConditionManager
     */
    public void initializeWinConditionManager() {
        this.winConditionManager = new WinConditionManager(plugin);
    }

    public WinConditionManager getWinConditionManager() {
        return winConditionManager;
    }

    public void toggleUHCState() {
        if (currentState == UHCState.RUNNING) {
            stopUHC();
        } else {
            startingUHC();
        }
    }

    private void startUHCNow() {
        currentState = UHCState.RUNNING;
        MessageUtils.sendBroadcastMessage("<green>El UHC ha comenzado!");
        applyGameRules(true);
        setPvP(false); // Disable PVP at the start
        scatterPlayers();
        worldBorderManager.setWorldBorder(configManager.getWorldBorderSize(), 0);
        if (uhcTimerManager != null) {
            uhcTimerManager.startUHCTimers();
        }
        // Reiniciar el WinConditionManager para el nuevo juego
        if (winConditionManager != null) {
            winConditionManager.reset();
        }
    }

    private void startingUHC() {
        currentState = UHCState.STARTING;
        MessageUtils.sendBroadcastMessage("<gold>El UHC comenzará en 15 segundos...");
        // Schedule countdown from 10 to 1 starting at 5 seconds delay (15 - 10)
        for (int i = 10; i >= 1; i--) {
            final int count = i;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                MessageUtils.broadcastTitle("<gold>" + count + "</gold>", "", 1, 2, 1);
            }, 20L * (15 - count));
        }
        // After countdown, broadcast "¡COMIENZA!", play dragon growl sound, and start the game
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            MessageUtils.broadcastTitle("<green><b>¡COMIENZA</b>!</green>", "", 1, 3, 1);
            SoundUtils.broadcastPlayerSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
            startUHCNow();
        }, 20L * 15);
    }

    private void stopUHC() {
        currentState = UHCState.STOPPED;
        MessageUtils.sendBroadcastMessage("<red>El UHC ha terminado!");
        applyGameRules(false);
        setPvP(true); // Enable PVP when stopped (or set to default)
        if (uhcTimerManager != null) {
            uhcTimerManager.stopUHCTimers();
        }
        worldBorderManager.resetWorldBorder();
        // Optionally, handle players after stopping, e.g., teleport back to spawn
    }

    private void applyGameRules(boolean isRunning) {
        boolean naturalRegeneration = !isRunning; // false when running, true when stopped
        boolean announceAdvancements = !isRunning; // false when running, true when stopped
        boolean fallDamage = isRunning; // true when running, false when stopped
        boolean daylightCycle = isRunning; // true when running, false when stopped
        boolean mobSpawning = isRunning; // true when running, false when stopped

        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.NATURAL_REGENERATION, naturalRegeneration);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, announceAdvancements);
            world.setGameRule(GameRule.FALL_DAMAGE, fallDamage);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, daylightCycle);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, mobSpawning);
            if(isRunning) {
                world.setTime(0);
            } else {
                world.setTime(6000);
            }
        }

    }

    /**
     * Sets the PVP game rule for all worlds.
     * @param enabled true to enable PVP, false to disable
     */
    @SuppressWarnings("unchecked")
    public void setPvP(boolean enabled) {
        GameRule<Boolean> pvpRule = (GameRule<Boolean>) GameRule.getByName("PVP");
        if (pvpRule != null) {
            for (World world : Bukkit.getWorlds()) {
                world.setGameRule(pvpRule, enabled);
            }
        }
    }

    private void scatterPlayers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        World world = Bukkit.getWorlds().get(0); // Assuming the first world is the main game world
        int borderRadius = configManager.getBorderRadius(); // Radio configurable desde config.yml
        Location center = world.getSpawnLocation(); // Centro del mundo

        // Filtrar solo jugadores en modo survival
        for (Player player : players) {
            if (player.getGameMode() == GameMode.SURVIVAL) {
                Location borderLoc = findBorderLocation(world, center, borderRadius);
                if (borderLoc != null) {
                    player.teleport(borderLoc);
                    MessageUtils.sendMessage(player, "<green>Has sido teletransportado al borde de la zona de juego.");
                } else {
                    MessageUtils.sendMessage(player, "<red>No se pudo encontrar una ubicación segura en el borde para teletransportarte.");
                }
            }
        }
    }

    /**
     * Encuentra una ubicación segura en el borde exacto del radio especificado.
     * Coloca al jugador en el bloque más alto disponible.
     */
    private Location findBorderLocation(World world, Location center, int radius) {
        int maxAttempts = configManager.getScatterMaxAttempts(); // Número configurable de intentos
        for (int i = 0; i < maxAttempts; i++) { // Intentar encontrar una ubicación segura
            // Generar un ángulo aleatorio para distribuir jugadores alrededor del borde
            double angle = random.nextDouble() * 2 * Math.PI;
            
            // Calcular coordenadas exactamente en el borde (distancia = radio)
            int x = (int) (center.getX() + radius * Math.cos(angle));
            int z = (int) (center.getZ() + radius * Math.sin(angle));
            
            // Obtener el bloque más alto en esas coordenadas
            int y = world.getHighestBlockYAt(x, z);

            Location potentialLoc = new Location(world, x + 0.5, y + 1, z + 0.5); // +0.5 para centrar en el bloque, +1 para estar encima

            // Verificar si la ubicación es segura (no dentro de un bloque)
            Block blockBelow = potentialLoc.getBlock().getRelative(BlockFace.DOWN);
            Block blockAt = potentialLoc.getBlock();
            Block blockAbove = potentialLoc.getBlock().getRelative(BlockFace.UP);

            if (blockBelow.getType().isSolid() && !blockAt.getType().isSolid() && !blockAbove.getType().isSolid()) {
                return potentialLoc;
            }
        }
        return null; // No se pudo encontrar una ubicación segura
    }

    /**
     * Termina el UHC con un ganador específico
     */
    public void endUHCWithWinner(Player winner) {
        currentState = UHCState.STOPPED;
        MessageUtils.sendBroadcastMessage("<gold>¡" + winner.getName() + " ha ganado el UHC!</gold>");
        applyGameRules(false);
        setPvP(true);
        if (uhcTimerManager != null) {
            uhcTimerManager.stopUHCTimers();
        }
        worldBorderManager.resetWorldBorder();
        
        // Los gamemodes serán manejados manualmente por el admin
    }

    /**
     * Termina el UHC sin ganador (empate)
     */
    public void endUHCWithoutWinner() {
        currentState = UHCState.STOPPED;
        MessageUtils.sendBroadcastMessage("<red>El UHC ha terminado sin ganador!</red>");
        applyGameRules(false);
        setPvP(true);
        if (uhcTimerManager != null) {
            uhcTimerManager.stopUHCTimers();
        }
        worldBorderManager.resetWorldBorder();
        
        // Los gamemodes serán manejados manualmente por el admin
    }
}
