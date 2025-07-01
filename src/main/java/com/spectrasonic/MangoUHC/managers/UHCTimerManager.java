package com.spectrasonic.MangoUHC.managers;

import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.Utils.MessageUtils;
import net.kyori.adventure.bossbar.BossBar;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UHCTimerManager {

    private final Main plugin;
    private final TimerManager timerManager;
    private final UHCGameManager gameManager;
    private final WorldBorderManager worldBorderManager;

    /**
     * Starts the specific timers for the UHC game.
     */
    public void startUHCTimers() {
        // 15-minute timer for PVP activation
        int pvpActivationTime = 15 * 60; // 15 minutes in seconds
        String pvpActivationMessage = "<red><b>PVP Activo en: </b><reset>";
        timerManager.createTimer(pvpActivationTime, BossBar.Color.RED, pvpActivationMessage, () -> {
            gameManager.setPvP(true);
            MessageUtils.sendBroadcastMessage("<red><b>¡PVP HA SIDO ACTIVADO!</b></red>");
        });

        // Timer for world border shrink
        int borderShrinkTime = 20 * 60; // 20 minutes in seconds
        String borderShrinkMessage = "<blue><b>El borde se encoge en: </b><reset>";
        timerManager.createTimer(borderShrinkTime, BossBar.Color.BLUE, borderShrinkMessage, () -> {
            MessageUtils.sendBroadcastMessage("<blue><b>¡El borde ha comenzado a encogerse!</b></blue>");
            worldBorderManager.setWorldBorder(300, 20 * 60); // Shrink to 150 radius over 20 minutes
        });

        // 1-hour timer for game duration
        int gameDurationTime = 60 * 60; // 1 hour in seconds
        String gameDurationMessage = "<green><b>Tiempo de juego: </b><reset>";
        timerManager.createTimer(gameDurationTime, BossBar.Color.GREEN, gameDurationMessage);
    }

    /**
     * Stops all UHC related timers.
     * This might be called when the UHC is stopped manually.
     */
    public void stopUHCTimers() {
        // If specific timer IDs were used, we could stop them here.
        // Since createTimer generates random IDs, stopping all timers is the simplest approach
        // based on the current TimerManager implementation.
        // If more fine-grained control is needed, we would need to store the timer IDs
        // created in startUHCTimers.
        timerManager.stopAllTimers();
    }
}