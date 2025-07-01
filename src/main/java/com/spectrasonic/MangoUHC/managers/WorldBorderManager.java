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