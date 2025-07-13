package com.spectrasonic.MangoUHC.listeners;

import com.spectrasonic.MangoUHC.Main;
import com.spectrasonic.MangoUHC.enums.UHCState;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;

/**
 * Listener para eventos relacionados con entidades
 * Maneja drops personalizados durante el UHC
 */
@RequiredArgsConstructor
public class EntityListener implements Listener {

    private final Main plugin;

    /**
     * Maneja la muerte de entidades para modificar drops durante el UHC
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // Solo modificar drops si el UHC está activo
        if (plugin.getUhcGameManager().getCurrentState() != UHCState.RUNNING) {
            return;
        }

        // Verificar si la entidad que murió es un Ghast
        if (event.getEntity().getType() == EntityType.GHAST) {
            handleGhastDeath(event);
        }
    }

    /**
     * Maneja el drop personalizado cuando muere un Ghast durante el UHC
     * Reemplaza lágrimas de ghast y pólvora por manzanas doradas
     */
    private void handleGhastDeath(EntityDeathEvent event) {
        Ghast ghast = (Ghast) event.getEntity();
        
        // Limpiar drops originales (lágrimas y pólvora)
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        int removedItems = 0;
        
        while (iterator.hasNext()) {
            ItemStack drop = iterator.next();
            if (drop.getType() == Material.GHAST_TEAR || drop.getType() == Material.GUNPOWDER) {
                removedItems += drop.getAmount();
                iterator.remove();
            }
        }
        
        // Solo agregar manzana dorada si se removieron items originales
        if (removedItems > 0) {
            // Agregar una manzana dorada por cada item removido (máximo 2)
            int goldenApples = Math.min(removedItems, 2);
            ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE, goldenApples);
            event.getDrops().add(goldenApple);

        }
    }

}