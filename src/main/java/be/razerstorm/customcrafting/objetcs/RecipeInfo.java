package be.razerstorm.customcrafting.objetcs;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class RecipeInfo {
    private @Getter String[] shape;
    private @Getter HashMap<Character, XMaterial> ingredients;
    private @Getter String recipeName;
    private @Getter ItemStack output;

    public RecipeInfo(String[] identifier, HashMap<Character, XMaterial> ingredients) {
        this.shape = identifier;
        this.ingredients = ingredients;
    }

    public RecipeInfo(String recipeName, ItemStack output, HashMap<Character, XMaterial> ingredients, String... identifier) {
        this.shape = identifier;
        this.ingredients = ingredients;
        this.recipeName = recipeName;
        this.output = output;
    }
}
