package com.spectrasonic.MangoUHC.commands.timer;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.utils.TimeParser;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.command.CommandSender;
import com.spectrasonic.Utils.MessageUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;

@CommandAlias("timer")
@CommandPermission("mangouhc.timer.*")
@RequiredArgsConstructor
public class TimerCommand extends BaseCommand {

    private final Main plugin;

    @Subcommand("create")
    @CommandPermission("mangouhc.timer.create")
    @Syntax("<time> <color> <message>")
    @Description("Creates a new timer with specified duration, color and message")
    @CommandCompletion("@time-examples @bossbar-colors _")
    public void onCreateTimer(CommandSender sender, String timeString, @Values("@bossbar-colors") String colorString, String... messageArgs) {
        int totalSeconds = TimeParser.parseTime(timeString);
        if (totalSeconds <= 0) {
            MessageUtils.sendMessage(sender, "<red>Formato de tiempo inválido! Usa un formato como: 1h30m15s, 45m, 30s");
            return;
        }

        BossBar.Color color;
        try {
            color = BossBar.Color.valueOf(colorString.toUpperCase());
        } catch (IllegalArgumentException e) {
            MessageUtils.sendMessage(sender, "<red>Color inválido! Colores disponibles: BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW");
            return;
        }

        // Join message parts
        String message = String.join(" ", messageArgs);
        if (message.trim().isEmpty()) {
            MessageUtils.sendMessage(sender, "<red>El mensaje no puede estar vacío!");
            return;
        }

        // Create timer with random ID
        String timerId = plugin.getTimerManager().createTimer(totalSeconds, color, message);
        MessageUtils.sendMessage(sender, "<green>Timer creado con éxito! ID: <gold>" + timerId);
    }

    /**
     * Adds time to a specific timer
     * Usage: /timer add <id> 5m30s
     */
    @Subcommand("add")
    @CommandPermission("mangouhc.timer.add")
    @Syntax("<timer_id> <time>")
    @Description("Adds time to a specific timer")
    @CommandCompletion("@timers @time-examples")
    public void onAddTime(CommandSender sender, String timerId, String timeString) {
        if (!plugin.getTimerManager().hasTimer(timerId)) {
            MessageUtils.sendMessage(sender, "<red>No se encontró un timer con ID: <gold>" + timerId);
            return;
        }

        int secondsToAdd = TimeParser.parseTime(timeString);
        if (secondsToAdd <= 0) {
            MessageUtils.sendMessage(sender, "<red>Formato de tiempo inválido! Usa un formato como: 1h30m15s, 45m, 30s");
            return;
        }

        plugin.getTimerManager().addTime(timerId, secondsToAdd);
        MessageUtils.sendMessage(sender, "<green>Añadidos " + TimeParser.formatTime(secondsToAdd) + " al timer <gold>" + timerId);
    }

    /**
     * Pauses a specific timer
     * Usage: /timer pause <id>
     */
    @Subcommand("pause")
    @CommandPermission("mangouhc.timer.pause")
    @Syntax("<timer_id>")
    @Description("Pauses a specific timer")
    @CommandCompletion("@timers")
    public void onPauseTimer(CommandSender sender, String timerId) {
        if (!plugin.getTimerManager().hasTimer(timerId)) {
            MessageUtils.sendMessage(sender, "<red>No se encontró un timer con ID: <gold>" + timerId);
            return;
        }

        if (plugin.getTimerManager().isPaused(timerId)) {
            MessageUtils.sendMessage(sender, "<red>El timer <gold>" + timerId + " <red>ya está pausado!");
            return;
        }

        plugin.getTimerManager().pauseTimer(timerId);
        MessageUtils.sendMessage(sender, "<green>Timer <gold>" + timerId + " <green>pausado!");
    }

    /**
     * Resumes a specific timer
     * Usage: /timer resume <id>
     */
    @Subcommand("resume")
    @CommandPermission("mangouhc.timer.pause")
    @Syntax("<timer_id>")
    @Description("Resumes a specific timer")
    @CommandCompletion("@timers")
    public void onResumeTimer(CommandSender sender, String timerId) {
        if (!plugin.getTimerManager().hasTimer(timerId)) {
            MessageUtils.sendMessage(sender, "<red>No se encontró un timer con ID: <gold>" + timerId);
            return;
        }

        if (!plugin.getTimerManager().isPaused(timerId)) {
            MessageUtils.sendMessage(sender, "<red>El timer <gold>" + timerId + " <red>ya está en ejecución!");
            return;
        }

        plugin.getTimerManager().resumeTimer(timerId);
        MessageUtils.sendMessage(sender, "<green>Timer <gold>" + timerId + " <green>reanudado!");
    }

    /**
     * Stops and removes a specific timer
     * Usage: /timer stop <id>
     */
    @Subcommand("stop")
    @CommandPermission("mangouhc.timer.stop")
    @Syntax("<timer_id>")
    @Description("Stops and removes a specific timer")
    @CommandCompletion("@timers")
    public void onStopTimer(CommandSender sender, String timerId) {
        if (!plugin.getTimerManager().hasTimer(timerId)) {
            MessageUtils.sendMessage(sender, "<red>No se encontró un timer con ID: <gold>" + timerId);
            return;
        }

        plugin.getTimerManager().stopTimer(timerId);
        MessageUtils.sendMessage(sender, "<green>Timer <gold>" + timerId + " <green>detenido!");
    }
    
    /**
     * Lists all active timers
     * Usage: /timer list
     */
    @Subcommand("list")
    @CommandPermission("mangouhc.timer.list")
    @Description("Lists all active timers")
    public void onListTimers(CommandSender sender) {
        List<String> timerIds = plugin.getTimerManager().getActiveTimerIds();
        
        if (timerIds.isEmpty()) {
            MessageUtils.sendMessage(sender, "<red>No hay timers activos!");
            return;
        }
        
        MessageUtils.sendMessage(sender, "<green>Timers activos: <gray>(" + timerIds.size() + ")");
        for (String id : timerIds) {
            boolean isPaused = plugin.getTimerManager().isPaused(id);
            String status = isPaused ? "<red>(Pausado)</red>" : "<green>(Activo)</green>";
            MessageUtils.sendMessage(sender, "<gray>- <gold>" + id + " " + status);
        }
    }
    
    /**
     * Stops all active timers
     * Usage: /timer stopall
     */
    @Subcommand("stopall")
    @CommandPermission("mangouhc.timer.stopall")
    @Description("Stops all active timers")
    public void onStopAllTimers(CommandSender sender) {
        List<String> timerIds = plugin.getTimerManager().getActiveTimerIds();
        
        if (timerIds.isEmpty()) {
            MessageUtils.sendMessage(sender, "<red>No hay timers activos!");
            return;
        }
        
        int count = timerIds.size();
        plugin.getTimerManager().stopAllTimers();
        MessageUtils.sendMessage(sender, "<green>Se detuvieron " + count + " timers!");
    }

}
