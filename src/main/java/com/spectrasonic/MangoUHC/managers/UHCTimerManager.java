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

        // 1-hour timer for PVP duration
        int pvpDurationTime = 60 * 60; // 1 hour in seconds
        String pvpDurationMessage = "<green><b>PVP Tiempo: </b><reset>";
        timerManager.createTimer(pvpDurationTime, BossBar.Color.GREEN, pvpDurationMessage);
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