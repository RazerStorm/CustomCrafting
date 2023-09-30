package be.razerstorm.customcrafting;

import be.razerstorm.customcrafting.utils.Metrics;

import com.cryptomorin.xseries.XItemStack;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class CustomCrafting extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        new Metrics(this, 19927);

        getConfig().getConfigurationSection("recipes").getKeys(false).forEach(key -> {
            Bukkit.getLogger().info("Loading recipe " + key);
            if (getConfig().get("recipes." + key) == null
                    || getConfig().get("recipes." + key + ".result") == null
                    || getConfig().get("recipes." + key + ".shape") == null
                    || getConfig().get("recipes." + key + ".ingredients") == null) {
                getLogger().warning("Recipe " + key + " is invalid!");
                return;
            }

            NamespacedKey recipeKey = new NamespacedKey(this, key);
            ShapedRecipe recipe = new ShapedRecipe(recipeKey, XItemStack.deserialize(getConfig().getConfigurationSection("recipes." + key + ".result")));

            List<String> shape = getConfig().getStringList("recipes." + key + ".shape");
            recipe.shape(shape.toArray(new String[0]));

            getConfig().getConfigurationSection("recipes." + key + ".ingredients").getKeys(false).forEach(ingredient -> {
                recipe.setIngredient(ingredient.charAt(0), XMaterial.valueOf(getConfig().getString("recipes." + key + ".ingredients." + ingredient)).parseMaterial());
            });

            getServer().addRecipe(recipe);
        });
    }
}