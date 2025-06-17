package com.spectrasonic.MangoUHC.managers;

import com.spectrasonic.MangoUHC.Main;
import lombok.RequiredArgsConstructor;

/**
 * Manages event listener registration
 */
@RequiredArgsConstructor
public class EventManager {

    private final Main plugin;

    /**
     * Registers all event listeners
     */
    public void registerEvents() {
        // Currently no events needed for this plugin
        // Add listeners here if needed in the future
    }
}
