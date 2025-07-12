package com.spectrasonic.MangoUHC.managers;

import co.aikar.commands.PaperCommandManager;
import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.commands.MangoCommand;
import com.spectrasonic.MangoUHC.commands.UHCCommand;
import com.spectrasonic.MangoUHC.commands.timer.TimerCommand;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommandManager {

    private final Main plugin;
    private PaperCommandManager commandManager;

    public void registerCommands() {
        this.commandManager = new PaperCommandManager(plugin);

        registerCompletions();

        commandManager.registerCommand(new TimerCommand(plugin));
        commandManager.registerCommand(new MangoCommand(plugin));
        commandManager.registerCommand(new UHCCommand(plugin.getUhcGameManager(), plugin.getConfigManager()));
    }

    private void registerCompletions() {
        List<String> colors = Arrays.asList(
            "BLUE", "GREEN", "PINK", "PURPLE", "RED", "WHITE", "YELLOW"
        );

        List<String> actions = Arrays.asList(
            "create", "add", "pause", "resume", "stop", "list", "stopall"
        );
        
        List<String> timeExamples = Arrays.asList(
            "10s", "30s", "1m", "5m", "10m", "30m", "1h", "2h", "1h30m", "1h30m15s"
        );

        commandManager.getCommandCompletions().registerStaticCompletion("bossbar-colors", colors);
        commandManager.getCommandCompletions().registerStaticCompletion("timer-actions", actions);
        commandManager.getCommandCompletions().registerStaticCompletion("time-examples", timeExamples);
        
        // Registro de completado dinámico para IDs de timers activos
        commandManager.getCommandCompletions().registerAsyncCompletion("timers", context -> {
            return plugin.getTimerManager().getActiveTimerIds();
        });
    }
}
