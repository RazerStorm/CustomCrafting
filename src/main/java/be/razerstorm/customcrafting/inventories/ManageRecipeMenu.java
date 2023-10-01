package be.razerstorm.customcrafting.inventories;

import be.razerstorm.customcrafting.CustomCrafting;
import be.razerstorm.customcrafting.managers.RecipeManager;
import be.razerstorm.customcrafting.objetcs.RecipeInfo;
import be.razerstorm.customcrafting.utils.ColorUtils;
import be.razerstorm.customcrafting.utils.GUIHolder;
import be.razerstorm.customcrafting.utils.ItemBuilder;
import co.aikar.commands.annotation.Optional;
import com.cryptomorin.xseries.XItemStack;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class ManageRecipeMenu extends GUIHolder {

    private final Player player;
    private final String recipeName;
    private final boolean editing;
    private final List<Integer> excludedSlots = Arrays.asList(10, 11, 12, 19, 20, 21, 23, 28, 29, 30);
    private final HashMap<Integer, Integer[]> rows = new HashMap<Integer, Integer[]>() {{
        put(1, new Integer[]{10, 11, 12});
        put(2, new Integer[]{19, 20, 21});
        put(3, new Integer[]{28, 29, 30});
    }};

    private final ItemStack invalid = new ItemBuilder(XMaterial.RED_WOOL.parseMaterial())
            .setColoredName("&4Submit")
            .addLoreLine(ColorUtils.color("&cThe recipe is invalid!"))
            .toItemStack();

    private final ItemStack valid = new ItemBuilder(XMaterial.GREEN_WOOL.parseMaterial())
            .setColoredName("&2Submit")
            .toItemStack();

    private final HashMap<Material, Character> ingredientsList = new HashMap<>();

    public ManageRecipeMenu(Player player, String recipeName, boolean editing) {
        this.player = player;
        this.recipeName = recipeName;
        this.editing = editing;
    }

    public void openMenu() {
        if(editing) {
            this.inventory = Bukkit.createInventory(this, 5 * 9, ColorUtils.color("&eEditing recipe: &6" + recipeName));
        }else {
            this.inventory = Bukkit.createInventory(this, 5 * 9, ColorUtils.color("&eCreating recipe: &6" + recipeName));
        }

        ItemStack item = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial())
                .setColoredName("&8")
                .toItemStack();

        Predicate<Integer> fill = (i) -> {
            if (excludedSlots.contains(i)) return false;
            inventory.setItem(i, item);
            return true;
        };

        if(editing) {
            RecipeInfo recipeInfo = RecipeManager.getInstance().getRecipeInfo(recipeName);
            ItemStack output = recipeInfo.getOutput();
            inventory.setItem(23, output);
            String[] shape = recipeInfo.getShape();
            HashMap<Character, XMaterial> ingredients = recipeInfo.getIngredients();

            for(int i = 0; i < shape.length; i++) {
                String row = shape[i];
                for(int j = 0; j < row.length(); j++) {
                    char c = row.charAt(j);
                    XMaterial material = ingredients.get(c);
                    if(material != null) {
                        inventory.setItem(rows.get(i + 1)[j], material.parseItem());
                    }
                }
            }
        }

        XItemStack.addItems(inventory, false, fill, item);
        inventory.setItem(25, invalid);



        open(player);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory() != event.getClickedInventory()) return;

        if (!excludedSlots.contains(event.getRawSlot())) event.setCancelled(true);

        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInstance(), () -> {

            boolean outputPresent = false;
            boolean inputPresent = false;

            if (event.getInventory().getItem(23) != null && event.getInventory().getItem(23).getType() != Material.AIR) {
                outputPresent = true;
            }

            for (int slot : excludedSlots) {
                if (slot == 23) continue;
                if (event.getInventory().getItem(slot) != null && event.getInventory().getItem(slot).getType() != Material.AIR) {
                    inputPresent = true;
                    break;
                }
            }

            if (outputPresent && inputPresent) {
                if (event.getRawSlot() == 25) {
                    submit();
                    return;
                }

                event.getInventory().setItem(25, valid);
            } else {
                event.getInventory().setItem(25, invalid);
            }

        }, 5L);
    }

    public void submit() {
        ItemStack output = inventory.getItem(23);
        RecipeInfo recipeInfo = getRecipeInfo();
        if(editing) {
            RecipeManager.getInstance().editRecipe(recipeName, output, recipeInfo.getIngredients(), recipeInfo.getShape());
            player.sendMessage(ColorUtils.color("&aSuccessfully edited recipe &2" + recipeName + "&a!"));
            player.closeInventory();
            return;
        }

        RecipeManager.getInstance().addRecipe(recipeName, output, recipeInfo.getIngredients(), recipeInfo.getShape());
        player.sendMessage(ColorUtils.color("&aSuccessfully created recipe &2" + recipeName + "&a!"));
        player.closeInventory();
    }

    public RecipeInfo getRecipeInfo() {
        String[] shapeArray = new String[3];
        HashMap<Character, XMaterial> ingredients = new HashMap<>();
        List<String> shape = new ArrayList<>();

        for (int row : rows.keySet()) {
            StringBuilder rowString = new StringBuilder();
            for (int slot : rows.get(row)) {
                ItemStack item = inventory.getItem(slot);
                if (item == null || item.getType() == Material.AIR) {
                    rowString.append(" ");
                    continue;
                }

                char ingredientLetter = getOrCreateIngredientLetter(item.getType());

                rowString.append(ingredientLetter);
                ingredients.put(ingredientLetter, XMaterial.matchXMaterial(item.getType()));
            }

            shape.add(rowString.toString());
        }

        shapeArray = shape.toArray(shapeArray);

        return new RecipeInfo(shapeArray, ingredients);
    }


    private char getOrCreateIngredientLetter(Material ingredient) {
        if (ingredientsList.containsKey(ingredient)) {
            return ingredientsList.get(ingredient);
        } else {
            char ingredientLetter = 'A';
            while (ingredientsList.containsValue(ingredientLetter)) {
                ingredientLetter++;
            }
            ingredientsList.put(ingredient, ingredientLetter);
            return ingredientLetter;
        }
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
    }
}