package be.razerstorm.customcrafting.inventories;

import be.razerstorm.customcrafting.CustomCrafting;
import be.razerstorm.customcrafting.enums.RecipeType;
import be.razerstorm.customcrafting.managers.RecipeManager;
import be.razerstorm.customcrafting.objects.RecipeInfo;
import be.razerstorm.customcrafting.utils.Utils;
import be.razerstorm.customcrafting.utils.GUIHolder;
import be.razerstorm.customcrafting.utils.ItemBuilder;
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

    private final RecipeType type;
    private int experience;
    private int cookingTime;

    private final Player player;
    private final String recipeName;
    private final boolean editing;
    private final List<Integer> craftingExcludedSlots = Arrays.asList(10, 11, 12, 19, 20, 21, 23, 28, 29, 30);
    private final List<Integer> furnaceExcludedSlots = Arrays.asList(10, 23, 28);
    private final HashMap<Integer, Integer[]> rows = new HashMap<Integer, Integer[]>() {{
        put(1, new Integer[]{10, 11, 12});
        put(2, new Integer[]{19, 20, 21});
        put(3, new Integer[]{28, 29, 30});
    }};

    private final ItemStack invalid = new ItemBuilder(XMaterial.RED_WOOL.parseMaterial())
            .setColoredName("&4Submit")
            .addLoreLine(Utils.color("&cThe recipe is invalid!"))
            .toItemStack();

    private final ItemStack valid = new ItemBuilder(XMaterial.GREEN_WOOL.parseMaterial())
            .setColoredName("&2Submit")
            .toItemStack();

    private final ItemStack invalidSlot = new ItemBuilder(XMaterial.RED_STAINED_GLASS_PANE.parseMaterial())
            .setColoredName("&4Invalid slot")
            .setLore(Utils.color("&cWe can't change the fuel of the recipe :("))
            .toItemStack();

    private final HashMap<Material, Character> ingredientsList = new HashMap<>();

    public ManageRecipeMenu(Player player, RecipeType type, String recipeName, boolean editing) {
        this.type = type;
        this.player = player;
        this.recipeName = recipeName;
        this.editing = editing;
    }

    public ManageRecipeMenu(Player player, RecipeType type, String recipeName, boolean editing, int experience, int cookingTime) {
        this.type = type;
        this.player = player;
        this.recipeName = recipeName;
        this.editing = editing;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    public void openMenu() {
        if (editing) {
            this.inventory = Bukkit.createInventory(this, 5 * 9, Utils.color("&eEditing recipe: &6" + recipeName));
        } else {
            this.inventory = Bukkit.createInventory(this, 5 * 9, Utils.color("&eCreating recipe: &6" + recipeName));
        }

        ItemStack item = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial())
                .setColoredName("&8")
                .toItemStack();

        Predicate<Integer> fill;

        switch (type) {
            case CRAFTING: {
                fill = (i) -> {
                    if (craftingExcludedSlots.contains(i)) return false;
                    inventory.setItem(i, item);
                    return true;
                };
                break;
            }
            case FURNACE: {
                fill = (i) -> {
                    if (i == 28) {
                        inventory.setItem(i, invalidSlot);
                        return true;
                    }

                    if (furnaceExcludedSlots.contains(i)) return false;

                    inventory.setItem(i, item);
                    return true;
                };
                break;
            }
            default: {
                fill = (i) -> {
                    return false;
                };
            }
        }

        XItemStack.addItems(inventory, false, fill, item);

        switch (type) {
            case CRAFTING: {
                if (!editing) break;
                RecipeInfo recipeInfo = RecipeManager.getInstance().getRecipeInfo(recipeName);
                ItemStack output = recipeInfo.getOutput();
                inventory.setItem(23, output);
                String[] shape = recipeInfo.getShape();
                HashMap<Character, ItemStack> ingredients = recipeInfo.getIngredients();

                for (int i = 0; i < shape.length; i++) {
                    String row = shape[i];
                    for (int j = 0; j < row.length(); j++) {
                        char c = row.charAt(j);
                        ItemStack ingredient = ingredients.get(c);
                        if (ingredient != null) {
                            inventory.setItem(rows.get(i + 1)[j], ingredient);
                        }
                    }
                }
                break;
            }
            case FURNACE: {
                if (!editing) break;
                ItemStack output = RecipeManager.getInstance().getOutput(recipeName);
                ItemStack ingredient = RecipeManager.getInstance().getIngredient(recipeName);

                inventory.setItem(23, output);
                inventory.setItem(10, ingredient);
                break;
            }
        }

        if (!editing) {
            inventory.setItem(25, invalid);
        } else {
            inventory.setItem(25, valid);
        }


        open(player);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory() == event.getClickedInventory()) {
            switch (type) {
                case CRAFTING: {
                    if (!craftingExcludedSlots.contains(event.getRawSlot())) event.setCancelled(true);
                    break;
                }
                case FURNACE: {
                    if (event.getRawSlot() == 28) event.setCancelled(true);
                    if(!furnaceExcludedSlots.contains(event.getRawSlot())) event.setCancelled(true);
                    break;
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(CustomCrafting.getInstance(), () -> {

            boolean outputPresent = false;
            boolean inputPresent = false;

            if (event.getInventory().getItem(23) != null && event.getInventory().getItem(23).getType() != Material.AIR) {
                outputPresent = true;
            }

            switch (type) {
                case CRAFTING: {
                    for (int slot : craftingExcludedSlots) {
                        if (slot == 23) continue;
                        if (event.getInventory().getItem(slot) != null && event.getInventory().getItem(slot).getType() != Material.AIR) {
                            inputPresent = true;
                            break;
                        }
                    }
                    break;
                }

                case FURNACE: {
                    if (event.getInventory().getItem(10) != null && event.getInventory().getItem(10).getType() != Material.AIR) {
                        inputPresent = true;
                    }
                    break;
                }
            }

            if (event.getRawSlot() == 25) {
                event.setCancelled(true);
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

        }, 8L);
    }

    public void submit() {
        ItemStack output = inventory.getItem(23);
        switch (type) {
            case CRAFTING: {
                RecipeInfo recipeInfo = getRecipeInfo();
                if (editing) {
                    RecipeManager.getInstance().editRecipe(recipeName, output, recipeInfo.getIngredients(), recipeInfo.getShape());
                    player.sendMessage(Utils.color("&aSuccessfully edited recipe &2" + recipeName + "&a!"));
                    player.closeInventory();
                } else {
                    RecipeManager.getInstance().addRecipe(recipeName, output, recipeInfo.getIngredients(), recipeInfo.getShape());
                    player.sendMessage(Utils.color("&aSuccessfully created recipe &2" + recipeName + "&a!"));
                }
                player.closeInventory();
                return;
            }
            case FURNACE: {
                if (editing) {
                    RecipeManager.getInstance().editRecipe(recipeName, output, inventory.getItem(10), experience, cookingTime);
                    player.sendMessage(Utils.color("&aSuccessfully edited recipe &2" + recipeName + "&a!"));
                    player.closeInventory();
                } else {
                    RecipeManager.getInstance().addRecipe(recipeName, output, inventory.getItem(10), experience, cookingTime);
                    player.sendMessage(Utils.color("&aSuccessfully created recipe &2" + recipeName + "&a!"));
                }
                player.closeInventory();
                return;
            }
        }
    }

    public RecipeInfo getRecipeInfo() {
        String[] shapeArray = new String[3];
        HashMap<Character, ItemStack> ingredients = new HashMap<>();
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
                ingredients.put(ingredientLetter, item);
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