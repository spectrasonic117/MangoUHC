package com.spectrasonic.MangoUHC.managers;

import co.aikar.commands.PaperCommandManager;
import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.commands.TimerCommand;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    private final Main plugin;
    private PaperCommandManager commandManager;

    public CommandManager(Main plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        this.commandManager = new PaperCommandManager(plugin);

        registerCompletions();

        commandManager.registerCommand(new TimerCommand(plugin));
    }

    private void registerCompletions() {
        List<String> colors = Arrays.asList(
            "BLUE", "GREEN", "PINK", "PURPLE", "RED", "WHITE", "YELLOW"
        );

        List<String> actions = Arrays.asList(
            "create", "add", "pause", "resume", "stop"
        );
        
        List<String> timeExamples = Arrays.asList(
            "10s", "30s", "1m", "5m", "10m", "30m", "1h", "2h", "1h30m", "1h30m15s"
        );

        commandManager.getCommandCompletions().registerStaticCompletion("bossbar-colors", colors);
        commandManager.getCommandCompletions().registerStaticCompletion("timer-actions", actions);
        commandManager.getCommandCompletions().registerStaticCompletion("time-examples", timeExamples);
    }
}
