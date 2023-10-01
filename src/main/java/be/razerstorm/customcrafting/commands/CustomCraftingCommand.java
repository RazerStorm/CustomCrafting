package be.razerstorm.customcrafting.commands;

import be.razerstorm.customcrafting.inventories.ManageRecipeMenu;
import be.razerstorm.customcrafting.managers.RecipeManager;
import be.razerstorm.customcrafting.utils.ColorUtils;
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
        sender.sendMessage(ColorUtils.color("&2/" + "cc" + " <subcommand> <arg>..."));
        getSubCommands().forEach((string, registeredCommand) -> {
            if (registeredCommand.getSyntaxText() == null || registeredCommand.getSyntaxText().isEmpty() || registeredCommand.getSyntaxText().equals(""))
                return;
            sender.sendMessage(ColorUtils.color("&a/" + "cc" + " " + registeredCommand.getSyntaxText()));
        });
    }

    @Subcommand("create")
    @Syntax("create <name>")
    @CommandPermission("customcrafting.command.create")
    public void onCreate(Player player, String recipeName) {
        if(RecipeManager.getInstance().recipeExists(recipeName)) {
            player.sendMessage(ColorUtils.color("&cRecipe already exists!"));
            return;
        }
        new ManageRecipeMenu(player, recipeName, false).openMenu();
    }

    @Subcommand("edit")
    @Syntax("edit <name>")
    @CommandCompletion("@recipes")
    @CommandPermission("customcrafting.command.edit")
    public void onEdit(Player player, String recipeName) {
        if (!RecipeManager.getInstance().recipeExists(recipeName)) {
            player.sendMessage(ColorUtils.color("&cRecipe not found!"));
            return;
        }
        new ManageRecipeMenu(player, recipeName, true).openMenu();
    }

    @Subcommand("delete")
    @Syntax("delete <name>")
    @CommandCompletion("@recipes")
    @CommandPermission("customcrafting.command.delete")
    public void onDelete(CommandSender sender, String recipeName) {
        if (RecipeManager.getInstance().getRecipes().contains(recipeName)) {
            RecipeManager.getInstance().deleteRecipe(recipeName);
            sender.sendMessage(ColorUtils.color("&aRecipe deleted!"));
        } else {
            sender.sendMessage(ColorUtils.color("&cRecipe not found!"));
        }
    }

    @Subcommand("list")
    @Syntax("list")
    @CommandPermission("customcrafting.command.list")
    public void onList(CommandSender sender) {
        sender.sendMessage(ColorUtils.color("&aRecipes:"));
        ArrayList<String> recipes = RecipeManager.getInstance().getRecipes();
        for (String recipe : recipes) {
            sender.sendMessage(ColorUtils.color("&a- " + recipe));
        }
    }
}