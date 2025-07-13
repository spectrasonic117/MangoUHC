package com.spectrasonic.MangoUHC.listeners;

import com.spectrasonic.MangoUHC.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import lombok.RequiredArgsConstructor;

import java.util.Random;

/**
 * Listener para eventos relacionados con bloques
 */
@RequiredArgsConstructor
public class BlockListener implements Listener {

    private final Main plugin;
    private final Random random = new Random();

    /**
     * Maneja el evento de romper bloques para agregar drops personalizados
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        // Verificar si el bloque es una hoja
        if (isLeafBlock(block.getType())) {
            handleLeafBreak(block, player);
        }
    }

    /**
     * Verifica si el material es un tipo de hoja
     */
    private boolean isLeafBlock(Material material) {
        return material == Material.OAK_LEAVES ||
               material == Material.BIRCH_LEAVES ||
               material == Material.SPRUCE_LEAVES ||
               material == Material.JUNGLE_LEAVES ||
               material == Material.ACACIA_LEAVES ||
               material == Material.DARK_OAK_LEAVES ||
               material == Material.MANGROVE_LEAVES ||
               material == Material.CHERRY_LEAVES ||
               material == Material.AZALEA_LEAVES ||
               material == Material.FLOWERING_AZALEA_LEAVES;
    }

    /**
     * Maneja el drop de manzanas cuando se rompe una hoja
     */
    private void handleLeafBreak(Block block, Player player) {
        double appleDropChance = plugin.getConfigManager().getAppleDropChance();
        
        // Generar numero aleatorio entre 0.0 y 1.0
        double randomValue = random.nextDouble();
        
        // Si el numero aleatorio es menor que la probabilidad configurada, dropear manzana
        if (randomValue < appleDropChance) {
            ItemStack apple = new ItemStack(Material.APPLE, 1);
            block.getWorld().dropItemNaturally(block.getLocation(), apple);
        }
    }
}