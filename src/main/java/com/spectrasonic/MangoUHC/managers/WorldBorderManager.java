package com.spectrasonic.MangoUHC.managers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public class WorldBorderManager {

    public void setWorldBorder(double size, long delay) {
        World world = Bukkit.getWorlds().get(0);
        if (world != null) {
            WorldBorder border = world.getWorldBorder();
            border.setCenter(world.getSpawnLocation());
            border.setSize(size, delay);
            
            // Configurar el daño del borde para evitar teletransporte
            border.setDamageAmount(1.0); // Daño por segundo fuera del borde
            border.setDamageBuffer(0.0); // Sin buffer de distancia para el daño
            border.setWarningDistance(10); // Advertencia a 10 bloques del borde
            border.setWarningTime(15); // Advertencia 15 segundos antes de que se encoja
        }
    }

    public void resetWorldBorder() {
        World world = Bukkit.getWorlds().get(0);
        if (world != null) {
            WorldBorder border = world.getWorldBorder();
            border.reset();
        }
    }
}