package com.spectrasonic.MangoUHC.recipes;

import com.spectrasonic.Utils.RecipeBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Clase para registrar la receta personalizada de manzana dorada
 * Receta: Cabeza de jugador en el centro rodeada de lingotes de oro
 */
public class GoldenAppleRecipe {
    
    /**
     * Registra la receta personalizada de manzana dorada
     * @param plugin El plugin principal
     */
    public static void registerGoldenAppleRecipe(Plugin plugin) {
        // Crear el resultado: una manzana dorada
        ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE, 1);
        
        // Crear los ingredientes
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemStack goldIngot = new ItemStack(Material.GOLD_INGOT, 1);
        
        // Crear la receta usando RecipeBuilder
        // Patrón: 
        // G G G
        // G H G  (donde H = cabeza del jugador, G = lingote de oro)
        // G G G
        new RecipeBuilder(plugin, "custom_golden_apple", goldenApple)
                .pattern("GGG", "GHG", "GGG")
                .ingredient('G', goldIngot)
                .ingredient('H', playerHead)
                .buildShaped();
    }
}