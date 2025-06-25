package com.spectrasonic.MangoUHC.managers;

import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.enums.UHCState;
import com.spectrasonic.Utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

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
    @NonNull @Setter private UHCTimerManager uhcTimerManager;
    private UHCState currentState = UHCState.STOPPED;
    private final Random random = new Random();

    public void toggleUHCState() {
        if (currentState == UHCState.RUNNING) {
            stopUHC();
        } else {
            startUHC();
        }
    }

    private void startUHC() {
        currentState = UHCState.RUNNING;
        MessageUtils.sendBroadcastMessage("<green>El UHC ha comenzado!");
        applyGameRules(true);
        setPvP(false); // Disable PVP at the start
        scatterPlayers();
        uhcTimerManager.startUHCTimers();
    }

    private void stopUHC() {
        currentState = UHCState.STOPPED;
        MessageUtils.sendBroadcastMessage("<red>El UHC ha terminado!");
        applyGameRules(false);
        setPvP(true); // Enable PVP when stopped (or set to default)
        uhcTimerManager.stopUHCTimers();
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
        int scatterRadius = 150;
        Location center = world.getSpawnLocation(); // Or a specific game zone center

        for (Player player : players) {
            Location scatterLoc = findSafeScatterLocation(world, center, scatterRadius);
            if (scatterLoc != null) {
                player.teleport(scatterLoc);
                MessageUtils.sendMessage(player, "<green>Has sido teletransportado a la zona de juego.");
            } else {
                MessageUtils.sendMessage(player, "<red>No se pudo encontrar una ubicación segura para teletransportarte.");
            }
        }
    }

    private Location findSafeScatterLocation(World world, Location center, int radius) {
        for (int i = 0; i < 50; i++) { // Try finding a safe location up to 50 times
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = random.nextDouble() * radius;

            int x = (int) (center.getX() + distance * Math.cos(angle));
            int z = (int) (center.getZ() + distance * Math.sin(angle));
            int y = world.getHighestBlockYAt(x, z);

            Location potentialLoc = new Location(world, x + 0.5, y + 1, z + 0.5); // +0.5 to center in block, +1 to be above block

            // Check if the location is safe (e.g., not inside a block)
            Block blockBelow = potentialLoc.getBlock().getRelative(BlockFace.DOWN);
            Block blockAt = potentialLoc.getBlock();
            Block blockAbove = potentialLoc.getBlock().getRelative(BlockFace.UP);

            if (blockBelow.getType().isSolid() && !blockAt.getType().isSolid() && !blockAbove.getType().isSolid()) {
                return potentialLoc;
            }
        }
        return null; // Could not find a safe location
    }
}
