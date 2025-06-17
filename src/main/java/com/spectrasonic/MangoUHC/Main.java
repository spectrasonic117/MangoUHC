package com.spectrasonic.MangoUHC;

import com.spectrasonic.MangoUHC.managers.CommandManager;
import com.spectrasonic.MangoUHC.managers.ConfigManager;
import com.spectrasonic.MangoUHC.managers.EventManager;
import com.spectrasonic.MangoUHC.managers.TimerManager;
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

    @Override
    public void onEnable() {

        this.configManager = new ConfigManager(this);
        this.timerManager = new TimerManager(this, MiniMessage.miniMessage());
        this.eventManager = new EventManager(this);
        this.commandManager = new CommandManager(this);

        configManager.loadConfig();
        commandManager.registerCommands();
        eventManager.registerEvents();

        CommandUtils.setPlugin(this);
        MessageUtils.sendStartupMessage(this);

    }

    @Override
    public void onDisable() {

        if (timerManager != null) {
            timerManager.cleanup();
        }

        MessageUtils.sendShutdownMessage(this);
    }

}