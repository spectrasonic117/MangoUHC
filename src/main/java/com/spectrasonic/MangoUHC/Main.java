package com.spectrasonic.MangoUHC;

import com.spectrasonic.MangoUHC.managers.CommandManager;
import com.spectrasonic.MangoUHC.managers.ConfigManager;
import com.spectrasonic.MangoUHC.managers.EventManager;
import com.spectrasonic.MangoUHC.managers.TimerManager;
import com.spectrasonic.MangoUHC.managers.UHCGameManager;
import com.spectrasonic.MangoUHC.managers.UHCTimerManager;
import com.spectrasonic.MangoUHC.recipes.GoldenAppleRecipe;
import com.spectrasonic.Utils.CommandUtils;
import com.spectrasonic.Utils.MessageUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Main extends JavaPlugin {
    
    private CommandManager commandManager;
    private EventManager eventManager;
    private ConfigManager configManager;
    private TimerManager timerManager;
    private UHCGameManager uhcGameManager;
    private UHCTimerManager uhcTimerManager;

    @Override
    public void onEnable() {

        initManagers();

        CommandUtils.setPlugin(this);
        MessageUtils.sendStartupMessage(this);
        MessageUtils.sendVeiMessage(this);

    }

    @Override
    public void onDisable() {

        if (timerManager != null) {
            timerManager.cleanup();
        }

        MessageUtils.sendShutdownMessage(this);
    }

    public void initManagers() {
        this.configManager = new ConfigManager(this);
        this.timerManager = new TimerManager(this, MiniMessage.miniMessage());
        this.eventManager = new EventManager(this);
        this.uhcGameManager = new UHCGameManager(this, configManager);
        this.uhcTimerManager = new UHCTimerManager(this, timerManager, uhcGameManager, uhcGameManager.getWorldBorderManager());
        uhcGameManager.setUhcTimerManager(uhcTimerManager);
        uhcGameManager.initializeWinConditionManager();
        this.commandManager = new CommandManager(this);

        configManager.loadConfig();
        commandManager.registerCommands();
        eventManager.registerEvents();
        
        // Registrar recetas personalizadas
        GoldenAppleRecipe.registerGoldenAppleRecipe(this);
    }

}