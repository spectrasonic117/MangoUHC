package com.spectrasonic.MangoUHC.utils;

import com.spectrasonic.Utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.ItemStack;

/**
 * Clase para manejar la creación de monumentos de muerte
 * Crea una valla con la cabeza del jugador cuando muere
 */
public class DeathMonumentManager {

    /**
     * Crea un monumento de muerte en la ubicación donde murió el jugador
     * @param player El jugador que murió
     * @param deathLocation La ubicación donde murió
     */
    public static void createDeathMonument(Player player, Location deathLocation) {
        if (deathLocation == null || deathLocation.getWorld() == null) {
            return;
        }

        try {
            // Obtener la ubicación del suelo (bloque sólido más cercano hacia abajo)
            Location groundLocation = findGroundLocation(deathLocation);
            
            if (groundLocation == null) {
                MessageUtils.sendMessage(player, "<red>No se pudo crear el monumento de muerte: ubicación inválida.");
                return;
            }

            // Crear la valla en el suelo
            Block fenceBlock = groundLocation.getBlock();
            fenceBlock.setType(Material.OAK_FENCE);

            // Crear la cabeza del jugador encima de la valla
            Block skullBlock = fenceBlock.getRelative(BlockFace.UP);
            skullBlock.setType(Material.PLAYER_HEAD);

            // Configurar la cabeza del jugador
            if (skullBlock.getState() instanceof Skull skull) {
                skull.setOwningPlayer(player);
                skull.update();
            }

            // Mensaje informativo
            String coords = String.format("X: %d, Y: %d, Z: %d", 
                groundLocation.getBlockX(), 
                groundLocation.getBlockY(), 
                groundLocation.getBlockZ());
            
            MessageUtils.sendBroadcastMessage(
                "<gray>Se ha creado un monumento para <yellow>" + player.getName() + 
                "</yellow> en " + coords + "</gray>"
            );

        } catch (Exception e) {
            MessageUtils.sendMessage(player, "<red>Error al crear el monumento de muerte: " + e.getMessage());
        }
    }

    /**
     * Encuentra la ubicación del suelo más cercana hacia abajo
     * @param location La ubicación inicial
     * @return La ubicación del suelo o null si no se encuentra
     */
    private static Location findGroundLocation(Location location) {
        Location searchLocation = location.clone();
        
        // Buscar hacia abajo hasta encontrar un bloque sólido
        for (int i = 0; i < 10; i++) { // Máximo 10 bloques hacia abajo
            Block block = searchLocation.getBlock();
            Block blockBelow = block.getRelative(BlockFace.DOWN);
            
            // Si el bloque de abajo es sólido y el actual no, esta es una buena ubicación
            if (blockBelow.getType().isSolid() && !block.getType().isSolid()) {
                return searchLocation;
            }
            
            // Si el bloque actual es sólido, buscar encima
            if (block.getType().isSolid()) {
                Location aboveLocation = searchLocation.clone().add(0, 1, 0);
                Block aboveBlock = aboveLocation.getBlock();
                
                // Si hay espacio arriba, usar esa ubicación
                if (!aboveBlock.getType().isSolid()) {
                    return aboveLocation;
                }
            }
            
            // Continuar buscando hacia abajo
            searchLocation.subtract(0, 1, 0);
        }
        
        // Si no se encuentra un lugar adecuado, usar la ubicación original
        return location;
    }

    /**
     * Verifica si se puede colocar un monumento en la ubicación especificada
     * @param location La ubicación a verificar
     * @return true si se puede colocar, false en caso contrario
     */
    public static boolean canPlaceMonument(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        Block block = location.getBlock();
        Block blockAbove = block.getRelative(BlockFace.UP);
        
        // Verificar que hay espacio para la valla y la cabeza
        return !block.getType().isSolid() && !blockAbove.getType().isSolid();
    }

    /**
     * Crea un ItemStack de cabeza de jugador (útil para otros usos)
     * @param player El jugador cuya cabeza crear
     * @return ItemStack de la cabeza del jugador
     */
    public static ItemStack createPlayerHead(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        
        if (meta != null) {
            meta.setOwningPlayer(player);
            meta.setDisplayName("§6Cabeza de " + player.getName());
            head.setItemMeta(meta);
        }
        
        return head;
    }
}