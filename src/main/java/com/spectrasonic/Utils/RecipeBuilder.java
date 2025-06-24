package com.spectrasonic.Utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeBuilder {
    private final Plugin plugin;
    private final String recipeName;
    private final ItemStack result;
    private final List<String> pattern = new ArrayList<>();
    private final Map<Character, ItemStack> ingredients = new HashMap<>();

    public RecipeBuilder(Plugin plugin, String recipeName, ItemStack result) {
        this.plugin = plugin;
        this.recipeName = recipeName;
        this.result = result;
    }

    public RecipeBuilder pattern(String... rows) {
        for (String row : rows) {
            pattern.add(row);
        }
        return this;
    }

    public RecipeBuilder ingredient(char symbol, ItemStack item) {
        ingredients.put(symbol, item);
        return this;
    }

    public void buildShaped() {
        NamespacedKey key = new NamespacedKey(plugin, recipeName);
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape(pattern.toArray(new String[0]));

        for (Map.Entry<Character, ItemStack> entry : ingredients.entrySet()) {
            recipe.setIngredient(entry.getKey(), entry.getValue().getType());
        }

        plugin.getServer().addRecipe(recipe);
    }

    public void buildShapeless() {
        NamespacedKey key = new NamespacedKey(plugin, recipeName);
        ShapelessRecipe recipe = new ShapelessRecipe(key, result);

        for (ItemStack item : ingredients.values()) {
            recipe.addIngredient(item.getType());
        }

        plugin.getServer().addRecipe(recipe);
    }
}


// ----- Use Example ----

// import org.bukkit.Material;
// import org.bukkit.inventory.ItemStack;
// import org.bukkit.plugin.java.JavaPlugin;

// public class MyPlugin extends JavaPlugin {
//     @Override
//     public void onEnable() {
//         // Crear ítems personalizados
//         ItemStack customItem = new ItemBuilder(Material.DIAMOND)
//                 .name("Espada Mágica")
//                 .lore("Una espada con poder arcano")
//                 .build();

//         // Crear receta Shaped
//         new RecipeBuilder(this, "magic_sword", customItem)
//                 .pattern(" D ", " D ", " S ")
//                 .ingredient("D", new ItemStack(Material.DIAMOND))
//                 .ingredient("S", new ItemStack(Material.STICK))
//                 .buildShaped();

//         // Crear receta Shapeless
//         new RecipeBuilder(this, "magic_sword_shapeless", customItem)
//                 .ingredient("D", new ItemStack(Material.DIAMOND))
//                 .ingredient("S", new ItemStack(Material.STICK))
//                 .buildShapeless();
//     }
// }
