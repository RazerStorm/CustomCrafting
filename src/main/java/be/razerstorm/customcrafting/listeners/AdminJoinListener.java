package be.razerstorm.customcrafting.listeners;

import be.razerstorm.customcrafting.utils.ColorUtils;
import be.razerstorm.customcrafting.utils.UpdateChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.UUID;

public class AdminJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("customcrafting.alerts")) {
            UpdateChecker updateChecker = UpdateChecker.getInstance();
            if (updateChecker.getWarnedPlayers().contains(event.getPlayer().getUniqueId())) return;

            if (updateChecker.isUpdateAvailable()) {
                player.sendMessage(ColorUtils.color("&2CustomCrafting &8» &7There is a new update available! Please download it at https://www.spigotmc.org/resources/customcrafting-create-your-own-recipes-1-12-1-20-2.112879/ !"));

                ArrayList<UUID> warnedPlayers = updateChecker.getWarnedPlayers();
                warnedPlayers.add(event.getPlayer().getUniqueId());
                updateChecker.setWarnedPlayers(warnedPlayers);
            }
        }
    }
}
