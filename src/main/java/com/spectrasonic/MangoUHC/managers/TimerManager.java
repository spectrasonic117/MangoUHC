package com.spectrasonic.MangoUHC.managers;

import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.Utils.SoundUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;


@RequiredArgsConstructor
public class TimerManager {

    private final Main plugin;
    private final MiniMessage miniMessage;
    private BossBar currentBossBar;
    private BukkitTask timerTask;
    private AtomicInteger remainingSeconds;
    private AtomicBoolean isPaused;
    private String timerMessage;

    {
        this.miniMessage = MiniMessage.miniMessage();
        this.remainingSeconds = new AtomicInteger(0);
        this.isPaused = new AtomicBoolean(false);
    }

    public void createTimer(int totalSeconds, BossBar.Color color, String message) {
        stopTimer();

        this.remainingSeconds.set(totalSeconds);
        this.timerMessage = message;
        this.isPaused.set(false);

        this.currentBossBar = BossBar.bossBar(
                Component.text(formatTimerDisplay(message, totalSeconds)),
                1.0f,
                color,
                BossBar.Overlay.PROGRESS
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showBossBar(currentBossBar);
        }

        startTimerTask();
    }


    public void addTime(int secondsToAdd) {
        if (currentBossBar == null) {
            return;
        }

        int newTotal = remainingSeconds.addAndGet(secondsToAdd);
        updateBossBar();
    }


    public void pauseTimer() {
        if (currentBossBar == null) {
            return;
        }

        isPaused.set(!isPaused.get());
    }


    public void stopTimer() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (currentBossBar != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.hideBossBar(currentBossBar);
            }
            currentBossBar = null;
        }

        remainingSeconds.set(0);
        isPaused.set(false);
    }


    public boolean hasActiveTimer() {
        return currentBossBar != null;
    }

    public boolean isPaused() {
        return isPaused.get();
    }

    private void startTimerTask() {
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (isPaused.get()) {
                    return;
                }

                int current = remainingSeconds.get();

                if (current <= 0) {
                    showFinishedMessage();
                    cancel();
                    return;
                }

                remainingSeconds.decrementAndGet();
                updateBossBar();
            }
        }.runTaskTimer(plugin, 20L, 20L); // Run every second
    }

    private void updateBossBar() {
        if (currentBossBar == null) {
            return;
        }

        int current = remainingSeconds.get();
        int originalTime = getOriginalTime();

        float progress = originalTime > 0 ? (float) current / originalTime : 0.0f;
        currentBossBar.progress(Math.max(0.0f, Math.min(1.0f, progress)));

        Component title = Component.text(formatTimerDisplay(timerMessage, current));
        currentBossBar.name(title);
    }

    private void showFinishedMessage() {
        if (currentBossBar == null) {
            return;
        }

        currentBossBar.name(miniMessage.deserialize("<red>Tiempo Terminado</red>"));
        currentBossBar.progress(0.0f);
        currentBossBar.color(BossBar.Color.RED);
        SoundUtils.broadcastPlayerSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        new BukkitRunnable() {
            @Override
            public void run() {
                stopTimer();
            }
        }.runTaskLater(plugin, 100L); // 5 seconds = 100 ticks
    }

    private String formatTimerDisplay(String message, int seconds) {
        String timeFormat = formatTime(seconds);
        return message + " " + timeFormat;
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private int getOriginalTime() {
        return remainingSeconds.get();
    }

    public void cleanup() {
        stopTimer();
    }
}
