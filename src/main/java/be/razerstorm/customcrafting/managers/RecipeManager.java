package be.razerstorm.customcrafting.managers;

import be.razerstorm.customcrafting.CustomCrafting;
import be.razerstorm.customcrafting.objetcs.RecipeInfo;
import com.cryptomorin.xseries.XItemStack;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.HashMap;

public class RecipeManager {

    private static RecipeManager instance;

    public void loadRecipes() {
        CustomCrafting customCrafting = CustomCrafting.getInstance();
        FileConfiguration config = customCrafting.getConfig();

        config.getConfigurationSection("recipes").getKeys(false).forEach(recipeName -> {
            Bukkit.getLogger().info("Loading recipe " + recipeName);
            if (config.get("recipes." + recipeName) == null
                    || config.get("recipes." + recipeName + ".result") == null
                    || config.get("recipes." + recipeName + ".shape") == null
                    || config.get("recipes." + recipeName + ".ingredients") == null) {
                customCrafting.getLogger().warning("Recipe " + recipeName + " is invalid!");
                return;
            }
            ItemStack output = XItemStack.deserialize(config.getConfigurationSection("recipes." + recipeName + ".result"));
            String[] shape = config.getStringList("recipes." + recipeName + ".shape").toArray(new String[0]);
            HashMap<Character, XMaterial> ingredients = new HashMap();

            config.getConfigurationSection("recipes." + recipeName + ".ingredients").getKeys(false).forEach(ingredientKey -> {
                XMaterial material = XMaterial.valueOf(config.getString("recipes." + recipeName + ".ingredients." + ingredientKey));
                ingredients.put(ingredientKey.charAt(0), material);
            });

            pushToServerRecipes(output, ingredients, new NamespacedKey(customCrafting, recipeName), shape);
        });
    }

    public void addRecipe(String recipeName, ItemStack output, HashMap<Character, XMaterial> ingredients, String... shape) {
        CustomCrafting customCrafting = CustomCrafting.getInstance();
        FileConfiguration config = customCrafting.getConfig();

        config.set("recipes." + recipeName + ".result", XItemStack.serialize(output));
        config.set("recipes." + recipeName + ".shape", shape);

        ingredients.forEach((identifier, ingredient) -> {
            config.set("recipes." + recipeName + ".ingredients." + identifier, ingredient.name());
        });

        customCrafting.saveConfig();

        pushToServerRecipes(output, ingredients, new NamespacedKey(customCrafting, recipeName), shape);
    }

    public void deleteRecipe(String recipeName) {
        CustomCrafting customCrafting = CustomCrafting.getInstance();
        FileConfiguration config = customCrafting.getConfig();

        config.set("recipes." + recipeName, null);
        customCrafting.saveConfig();

        CustomCrafting.getInstance().getServer().removeRecipe(new NamespacedKey(customCrafting, recipeName));
    }

    public void editRecipe(String recipeName, ItemStack output, HashMap<Character, XMaterial> ingredients, String... shape) {
        CustomCrafting customCrafting = CustomCrafting.getInstance();
        FileConfiguration config = customCrafting.getConfig();

        config.set("recipes." + recipeName, null);

        config.set("recipes." + recipeName + ".result", XItemStack.serialize(output));
        config.set("recipes." + recipeName + ".shape", shape);

        ingredients.forEach((identifier, ingredient) -> {
            config.set("recipes." + recipeName + ".ingredients." + identifier, ingredient.name());
        });

        customCrafting.saveConfig();
        CustomCrafting.getInstance().reloadConfig();

        NamespacedKey recipeKey = new NamespacedKey(customCrafting, recipeName);

        customCrafting.getServer().removeRecipe(recipeKey);
        pushToServerRecipes(output, ingredients, recipeKey, shape);
    }

    public void pushToServerRecipes(ItemStack output, HashMap<Character, XMaterial> ingredients, NamespacedKey recipeKey, String... shape) {
        CustomCrafting customCrafting = CustomCrafting.getInstance();
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, output);
        recipe.shape(shape);

        ingredients.forEach((identifier, ingredient) -> {
            recipe.setIngredient(identifier, ingredient.parseMaterial());
        });

        customCrafting.getServer().addRecipe(recipe);
    }

    public ArrayList<String> getRecipes () {
        CustomCrafting customCrafting = CustomCrafting.getInstance();
        FileConfiguration config = customCrafting.getConfig();

        return new ArrayList<>(config.getConfigurationSection("recipes").getKeys(false));
    }

    public RecipeInfo getRecipeInfo(String recipeName) {
        CustomCrafting customCrafting = CustomCrafting.getInstance();
        FileConfiguration config = customCrafting.getConfig();

        ItemStack output = XItemStack.deserialize(config.getConfigurationSection("recipes." + recipeName + ".result"));
        String[] shape = config.getStringList("recipes." + recipeName + ".shape").toArray(new String[0]);
        HashMap<Character, XMaterial> ingredients = new HashMap();

        config.getConfigurationSection("recipes." + recipeName + ".ingredients").getKeys(false).forEach(ingredientKey -> {
            XMaterial material = XMaterial.valueOf(config.getString("recipes." + recipeName + ".ingredients." + ingredientKey));
            ingredients.put(ingredientKey.charAt(0), material);
        });

        return new RecipeInfo(recipeName, output, ingredients, shape);
    }

    public boolean recipeExists(String recipe) {
        CustomCrafting customCrafting = CustomCrafting.getInstance();
        FileConfiguration config = customCrafting.getConfig();

        return config.get("recipes." + recipe) != null;
    }

    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }
}
