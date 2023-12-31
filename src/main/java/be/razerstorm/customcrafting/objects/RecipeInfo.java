package be.razerstorm.customcrafting.objects;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class RecipeInfo {
    private @Getter String[] shape;
    private @Getter HashMap<Character, ItemStack> ingredients;
    private @Getter ItemStack ingredient;
    private @Getter String recipeName;
    private @Getter ItemStack output;

    public RecipeInfo(String[] identifier, HashMap<Character, ItemStack> ingredients) {
        this.shape = identifier;
        this.ingredients = ingredients;
    }

    public RecipeInfo(String recipeName, ItemStack output, HashMap<Character, ItemStack> ingredients, String... identifier) {
        this.shape = identifier;
        this.ingredients = ingredients;
        this.recipeName = recipeName;
        this.output = output;
    }
}
