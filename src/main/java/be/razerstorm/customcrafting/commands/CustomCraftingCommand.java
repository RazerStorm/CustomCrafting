package be.razerstorm.customcrafting.commands;

import be.razerstorm.customcrafting.enums.RecipeType;
import be.razerstorm.customcrafting.inventories.ManageRecipeMenu;
import be.razerstorm.customcrafting.managers.RecipeManager;
import be.razerstorm.customcrafting.utils.Utils;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@CommandAlias("customcrafting|cc")
@CommandPermission("customcrafting.command")
public class CustomCraftingCommand extends BaseCommand {
    @Default
    public void onDefault(CommandSender sender) {
        sender.sendMessage(Utils.color("&2/" + "cc" + " <subcommand> <arg>..."));
        getSubCommands().forEach((string, registeredCommand) -> {
            if (registeredCommand.getSyntaxText() == null || registeredCommand.getSyntaxText().isEmpty() || registeredCommand.getSyntaxText().equals(""))
                return;
            sender.sendMessage(Utils.color("&a/" + "cc" + " " + registeredCommand.getSyntaxText()));
        });
    }

    @Subcommand("create")
    @Syntax("create <type> <name> <args>")
    @CommandPermission("customcrafting.command.create")
    public void onCreate(Player player, RecipeType type, String recipeName, @Optional String... args) {
        if (RecipeManager.getInstance().recipeExists(recipeName)) {
            player.sendMessage(Utils.color("&cRecipe already exists!"));
            return;
        }

        switch (type) {
            case CRAFTING:
                new ManageRecipeMenu(player, type, recipeName, false).openMenu();
                break;
            case FURNACE:
                if (args.length == 2) {
                    if (Utils.isInteger(args[0]) && Utils.isInteger(args[1])) {
                        new ManageRecipeMenu(player, type, recipeName, false, Integer.parseInt(args[0]), Integer.parseInt(args[1]) * 20).openMenu();
                        return;
                    }
                    player.sendMessage(Utils.color("&cInvalid arguments!"));
                } else if (args.length == 1) {
                    if (Utils.isInteger(args[0])) {
                        new ManageRecipeMenu(player, type, recipeName, false, Integer.parseInt(args[0]), 5 * 20).openMenu();
                        return;
                    }
                    player.sendMessage(Utils.color("&cInvalid arguments!"));
                } else {
                    new ManageRecipeMenu(player, type, recipeName, false, 0, 5 * 20).openMenu();
                }
                break;

            // ANVIL: Anvil support coming later because it doesn't have a recipes system
        }
    }

    @Subcommand("edit")
    @Syntax("edit <name>")
    @CommandCompletion("@recipes")
    @CommandPermission("customcrafting.command.edit")
    public void onEdit(Player player, String recipeName) {
        if (!RecipeManager.getInstance().recipeExists(recipeName)) {
            player.sendMessage(Utils.color("&cRecipe not found!"));
            return;
        }

        RecipeType type = RecipeManager.getInstance().getType(recipeName);

        switch (type) {
            case CRAFTING:
                new ManageRecipeMenu(player, type, recipeName, true).openMenu();
                break;
            case FURNACE:
                new ManageRecipeMenu(player, type, recipeName, true, RecipeManager.getInstance().getExperience(recipeName), RecipeManager.getInstance().getCookingTime(recipeName)).openMenu();
                break;

            // ANVIL: Anvil support coming later because it doesn't have a recipes system
        }
    }

    @Subcommand("delete")
    @Syntax("delete <name>")
    @CommandCompletion("@recipes")
    @CommandPermission("customcrafting.command.delete")
    public void onDelete(CommandSender sender, String recipeName) {
        if (RecipeManager.getInstance().getRecipes().contains(recipeName)) {
            RecipeManager.getInstance().deleteRecipe(recipeName);
            sender.sendMessage(Utils.color("&aRecipe deleted!"));
        } else {
            sender.sendMessage(Utils.color("&cRecipe not found!"));
        }
    }

    @Subcommand("list")
    @Syntax("list")
    @CommandPermission("customcrafting.command.list")
    public void onList(CommandSender sender) {
        sender.sendMessage(Utils.color("&aRecipes:"));
        ArrayList<String> recipes = RecipeManager.getInstance().getRecipes();
        for (String recipe : recipes) {
            sender.sendMessage(Utils.color("&a- " + recipe));
        }
    }
}