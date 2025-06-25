package com.spectrasonic.MangoUHC.managers;

import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.Utils.ColorConverter;
import com.spectrasonic.Utils.SoundUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.Runnable;


@RequiredArgsConstructor
public class TimerManager {

    private final Main plugin;
    private final MiniMessage miniMessage;
    private final Random random = new Random();
    
    // Mapa para almacenar múltiples timers por ID
    @Getter
    private final Map<String, TimerData> timers = new ConcurrentHashMap<>();
    
    /**
     * Clase interna para almacenar los datos de cada timer
     */
    public class TimerData {
        private final String id;
        private final BossBar bossBar;
        private final BukkitTask task;
        private final AtomicInteger remainingSeconds;
        private final AtomicBoolean isPaused;
        private final String message;
        private final Runnable completionCallback;

        public TimerData(String id, BossBar bossBar, BukkitTask task, int seconds, String message, Runnable completionCallback) {
            this.id = id;
            this.bossBar = bossBar;
            this.task = task;
            this.remainingSeconds = new AtomicInteger(seconds);
            this.isPaused = new AtomicBoolean(false);
            this.message = message;
            this.completionCallback = completionCallback;
        }

        public Runnable getCompletionCallback() {
            return completionCallback;
        }

        public String getId() {
            return id;
        }
        
        public BossBar getBossBar() {
            return bossBar;
        }
        
        public BukkitTask getTask() {
            return task;
        }
        
        public AtomicInteger getRemainingSeconds() {
            return remainingSeconds;
        }
        
        public AtomicBoolean getIsPaused() {
            return isPaused;
        }
        
        public String getMessage() {
            return message;
        }
        
        public int getOriginalTime() {
            return remainingSeconds.get();
        }
    }

    /**
     * Genera un ID aleatorio de 5 caracteres alfanuméricos
     * @return ID aleatorio
     */
    public String generateRandomId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // Asegurarse de que el ID sea único
        if (timers.containsKey(id.toString())) {
            return generateRandomId();
        }
        
        return id.toString();
    }
    
    /**
     * Crea un nuevo timer con un ID generado automáticamente
     * @param totalSeconds Duración total en segundos
     * @param color Color del BossBar
     * @param message Mensaje a mostrar
     * @return ID del timer creado
     */
    public String createTimer(int totalSeconds, BossBar.Color color, String message) {
        String timerId = generateRandomId();
        return createTimerWithId(timerId, totalSeconds, color, message, null);
    }

    /**
     * Crea un nuevo timer con un ID generado automáticamente y un callback de finalización
     * @param totalSeconds Duración total en segundos
     * @param color Color del BossBar
     * @param message Mensaje a mostrar
     * @param completionCallback Callback a ejecutar al finalizar el timer (puede ser null)
     * @return ID del timer creado
     */
    public String createTimer(int totalSeconds, BossBar.Color color, String message, Runnable completionCallback) {
        String timerId = generateRandomId();
        return createTimerWithId(timerId, totalSeconds, color, message, completionCallback);
    }

    /**
     * Crea un nuevo timer con un ID específico
     * @param timerId ID del timer
     * @param totalSeconds Duración total en segundos
     * @param color Color del BossBar
     * @param message Mensaje a mostrar
     * @param completionCallback Callback a ejecutar al finalizar el timer (puede ser null)
     * @return ID del timer creado
     */
    public String createTimerWithId(String timerId, int totalSeconds, BossBar.Color color, String message, Runnable completionCallback) {
        // Crear el BossBar
        BossBar bossBar = BossBar.bossBar(
                miniMessage.deserialize(formatTimerDisplay(message, totalSeconds)),
                1.0f,
                color,
                BossBar.Overlay.PROGRESS
        );
        
        // Mostrar el BossBar a todos los jugadores
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showBossBar(bossBar);
        }
        
        // Iniciar la tarea del timer
        BukkitTask task = startTimerTask(timerId, bossBar, totalSeconds, message, completionCallback);

        // Almacenar los datos del timer
        timers.put(timerId, new TimerData(timerId, bossBar, task, totalSeconds, message, completionCallback));

        return timerId;
    }


    /**
     * Añade tiempo a un timer específico
     * @param timerId ID del timer
     * @param secondsToAdd Segundos a añadir
     * @return true si se añadió el tiempo correctamente, false si el timer no existe
     */
    public boolean addTime(String timerId, int secondsToAdd) {
        TimerData timer = timers.get(timerId);
        if (timer == null) {
            return false;
        }

        timer.getRemainingSeconds().addAndGet(secondsToAdd);
        updateBossBar(timerId);
        return true;
    }


    /**
     * Pausa un timer específico
     * @param timerId ID del timer
     * @return true si se pausó correctamente, false si el timer no existe o ya está pausado
     */
    public boolean pauseTimer(String timerId) {
        TimerData timer = timers.get(timerId);
        if (timer == null || timer.getIsPaused().get()) {
            return false;
        }

        timer.getIsPaused().set(true);
        return true;
    }

    /**
     * Reanuda un timer específico
     * @param timerId ID del timer
     * @return true si se reanudó correctamente, false si el timer no existe o ya está en ejecución
     */
    public boolean resumeTimer(String timerId) {
        TimerData timer = timers.get(timerId);
        if (timer == null || !timer.getIsPaused().get()) {
            return false;
        }

        timer.getIsPaused().set(false);
        return true;
    }


    /**
     * Detiene y elimina un timer específico
     * @param timerId ID del timer
     * @return true si se detuvo correctamente, false si el timer no existe
     */
    public boolean stopTimer(String timerId) {
        TimerData timer = timers.get(timerId);
        if (timer == null) {
            return false;
        }

        // Cancelar la tarea
        if (timer.getTask() != null) {
            timer.getTask().cancel();
        }

        // Ocultar el BossBar
        if (timer.getBossBar() != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.hideBossBar(timer.getBossBar());
            }
        }

        // Eliminar el timer del mapa
        timers.remove(timerId);
        return true;
    }
    
    /**
     * Detiene y elimina todos los timers activos
     */
    public void stopAllTimers() {
        for (String timerId : new ArrayList<>(timers.keySet())) {
            stopTimer(timerId);
        }
    }


    /**
     * Verifica si existe un timer con el ID especificado
     * @param timerId ID del timer
     * @return true si existe, false en caso contrario
     */
    public boolean hasTimer(String timerId) {
        return timers.containsKey(timerId);
    }
    
    /**
     * Verifica si hay algún timer activo
     * @return true si hay al menos un timer activo, false en caso contrario
     */
    public boolean hasActiveTimers() {
        return !timers.isEmpty();
    }

    /**
     * Verifica si un timer específico está pausado
     * @param timerId ID del timer
     * @return true si está pausado, false si no está pausado o no existe
     */
    public boolean isPaused(String timerId) {
        TimerData timer = timers.get(timerId);
        return timer != null && timer.getIsPaused().get();
    }
    
    /**
     * Obtiene una lista de IDs de todos los timers activos
     * @return Lista de IDs de timers
     */
    public List<String> getActiveTimerIds() {
        return new ArrayList<>(timers.keySet());
    }
    
    /**
     * Muestra todos los timers activos a un jugador específico
     * @param player Jugador al que mostrar los timers
     */
    public void showAllTimersToPlayer(Player player) {
        if (player == null || timers.isEmpty()) {
            return;
        }
        
        for (TimerData timer : timers.values()) {
            player.showBossBar(timer.getBossBar());
        }
    }

    /**
     * Inicia la tarea del timer
     * @param timerId ID del timer
     * @param bossBar BossBar asociado al timer
     * @param totalSeconds Duración total en segundos
     * @param message Mensaje a mostrar
     * @param completionCallback Callback a ejecutar al finalizar el timer (puede ser null)
     * @return Tarea creada
     */
    private BukkitTask startTimerTask(String timerId, BossBar bossBar, int totalSeconds, String message, Runnable completionCallback) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                TimerData timer = timers.get(timerId);
                if (timer == null) {
                    cancel();
                    return;
                }
                
                if (timer.getIsPaused().get()) {
                    return;
                }

                int current = timer.getRemainingSeconds().get();

                if (current <= 0) {
                    showFinishedMessage(timerId);
                    if (completionCallback != null) {
                        completionCallback.run();
                    }
                    cancel();
                    return;
                }

                timer.getRemainingSeconds().decrementAndGet();
                updateBossBar(timerId);
            }
        }.runTaskTimer(plugin, 20L, 20L); // Run every second
    }

    /**
     * Actualiza el BossBar de un timer específico
     * @param timerId ID del timer
     */
    private void updateBossBar(String timerId) {
        TimerData timer = timers.get(timerId);
        if (timer == null) {
            return;
        }

        int current = timer.getRemainingSeconds().get();
        int originalTime = timer.getOriginalTime();

        float progress = originalTime > 0 ? (float) current / originalTime : 0.0f;
        timer.getBossBar().progress(Math.max(0.0f, Math.min(1.0f, progress)));

        // Usar miniMessage para deserializar el texto con formato
        Component title = miniMessage.deserialize(formatTimerDisplay(timer.getMessage(), current));
        timer.getBossBar().name(title);
    }

    /**
     * Muestra el mensaje de finalización para un timer específico
     * @param timerId ID del timer
     */
    private void showFinishedMessage(String timerId) {
        TimerData timer = timers.get(timerId);
        if (timer == null) {
            return;
        }

        timer.getBossBar().name(miniMessage.deserialize(ColorConverter.convertToMiniMessage("&cTiempo Terminado")));
        timer.getBossBar().progress(0.0f);
        timer.getBossBar().color(BossBar.Color.RED);
        SoundUtils.broadcastPlayerSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        final String finalTimerId = timerId;
        new BukkitRunnable() {
            @Override
            public void run() {
                stopTimer(finalTimerId);
            }
        }.runTaskLater(plugin, 100L); // 5 seconds = 100 ticks
    }

    private String formatTimerDisplay(String message, int seconds) {
        String timeFormat = formatTime(seconds);
        // Convertir códigos de color de Minecraft a formato MiniMessage
        return ColorConverter.convertToMiniMessage(message + " " + timeFormat);
    }

    private String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * Limpia todos los recursos al desactivar el plugin
     */
    public void cleanup() {
        stopAllTimers();
    }
}
