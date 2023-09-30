package be.razerstorm.customcrafting.objetcs;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;

import java.util.HashMap;

public class RecipeInfo {
    private @Getter String[] identifier;
    private @Getter HashMap<Character, XMaterial> ingredients;

    public RecipeInfo(String[] identifier, HashMap<Character, XMaterial> ingredients) {
        this.identifier = identifier;
        this.ingredients = ingredients;
    }
}
