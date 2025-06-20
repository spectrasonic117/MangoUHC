package com.spectrasonic.MangoUHC.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.utils.TimeParser;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.command.CommandSender;
import com.spectrasonic.Utils.MessageUtils;

@CommandAlias("timer")
public class TimerCommand extends BaseCommand {

    private final Main plugin;

    public TimerCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Subcommand("create")
    @CommandPermission("timer.create")
    @Syntax("<time> <color> <message>")
    @Description("Creates a new timer with specified duration, color and message")
    @CommandCompletion("@time-examples @bossbar-colors _")
    public void onCreateTimer(CommandSender sender, String timeString, @Values("@bossbar-colors") String colorString, String... messageArgs) {
        if (plugin.getTimerManager().hasActiveTimer()) {
            MessageUtils.sendMessage(sender, "<red>A timer is already active! Use /timer stop to remove it first.");
            return;
        }

        int totalSeconds = TimeParser.parseTime(timeString);
        if (totalSeconds <= 0) {
            MessageUtils.sendMessage(sender, "<red>Invalid time format! Use format like: 1h30m15s, 45m, 30s");

            return;
        }

        BossBar.Color color;
        try {
            color = BossBar.Color.valueOf(colorString.toUpperCase());
        } catch (IllegalArgumentException e) {
            MessageUtils.sendMessage(sender, "<red>Invalid color! Available colors: BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW");
            return;
        }

        // Join message parts
        String message = String.join(" ", messageArgs);
        if (message.trim().isEmpty()) {
            MessageUtils.sendMessage(sender, "<red>Message cannot be empty!");
            return;
        }

        // Create timer
        plugin.getTimerManager().createTimer(totalSeconds, color, message);
        MessageUtils.sendMessage(sender, "<green>Timer created successfully!");
    }

    /**
     * Adds time to the current timer
     * Usage: /timer add 5m30s
     */
    @Subcommand("add")
    @CommandPermission("timer.add")
    @Syntax("<time>")
    @Description("Adds time to the current timer")
    @CommandCompletion("@time-examples")
    public void onAddTime(CommandSender sender, String timeString) {
        if (!plugin.getTimerManager().hasActiveTimer()) {
            MessageUtils.sendMessage(sender, "<red>No active timer found!");
            return;
        }

        int secondsToAdd = TimeParser.parseTime(timeString);
        if (secondsToAdd <= 0) {
            MessageUtils.sendMessage(sender, "<red>Invalid time format! Use format like: 1h30m15s, 45m, 30s");
            return;
        }

        plugin.getTimerManager().addTime(secondsToAdd);
        MessageUtils.sendMessage(sender, "<green>Added " + TimeParser.formatTime(secondsToAdd) + " to the timer!");
    }

    /**
     * Pauses the current timer
     * Usage: /timer pause
     */
    @Subcommand("pause")
    @CommandPermission("timer.pause")
    @Description("Pauses the current timer")
    public void onPauseTimer(CommandSender sender) {
        if (!plugin.getTimerManager().hasActiveTimer()) {
            MessageUtils.sendMessage(sender, "<red>No active timer found!");
            return;
        }

        if (plugin.getTimerManager().isPaused()) {
            MessageUtils.sendMessage(sender, "<red>Timer is already paused!");
            return;
        }

        plugin.getTimerManager().pauseTimer();
        MessageUtils.sendMessage(sender, "<green>Timer paused!");
    }

    /**
     * Resumes the current timer
     * Usage: /timer resume
     */
    @Subcommand("resume")
    @CommandPermission("timer.pause")
    @Description("Resumes the current timer")
    public void onResumeTimer(CommandSender sender) {
        if (!plugin.getTimerManager().hasActiveTimer()) {
            MessageUtils.sendMessage(sender, "<red>No active timer found!");
            return;
        }

        if (!plugin.getTimerManager().isPaused()) {
            MessageUtils.sendMessage(sender, "<red>Timer is already running!");
            return;
        }

        plugin.getTimerManager().resumeTimer();
        MessageUtils.sendMessage(sender, "<green>Timer resumed!");
    }

    /**
     * Stops and removes the current timer
     * Usage: /timer stop
     */
    @Subcommand("stop")
    @CommandPermission("timer.stop")
    @Description("Stops and removes the current timer")
    public void onStopTimer(CommandSender sender) {
        if (!plugin.getTimerManager().hasActiveTimer()) {
            MessageUtils.sendMessage(sender, "<red>No active timer found!");
            return;
        }

        plugin.getTimerManager().stopTimer();
        MessageUtils.sendMessage(sender, "<green>Timer stopped!");
    }

}
