package be.razerstorm.customcrafting.utils;

import be.razerstorm.customcrafting.CustomCrafting;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

public class UpdateChecker {

    private static UpdateChecker instance;
    private @Getter
    @Setter ArrayList<UUID> warnedPlayers = new ArrayList<>();
    private @Getter boolean UpdateAvailable;

    public void checkForUpdate() {

        Bukkit.getScheduler().runTaskAsynchronously(CustomCrafting.getInstance(), () -> {
            try {
                URL apiUrl = new URL("https://api.spiget.org/v2/resources/112879/versions/latest");

                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                Logger logger = CustomCrafting.getInstance().getLogger();

                if (responseCode == 200) {
                    logger.info("UpdateChecker: HTTP_OK");
                } else {
                    logger.warning("UpdateChecker: HTTP_OTHER");
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(response.toString());
                String latestVersion = (String) jsonObject.get("name");

                String currentVersion = CustomCrafting.getInstance().getDescription().getVersion();

                if (latestVersion.equals(currentVersion)) {
                    UpdateAvailable = false;
                    logger.info("UpdateChecker: No update available");
                } else {
                    UpdateAvailable = true;
                    logger.warning("UpdateChecker: There is a new update available! (" + currentVersion + " -> " + latestVersion + ")");
                    logger.warning("Please download it at https://www.spigotmc.org/resources/customcrafting-create-your-own-recipes-1-12-1-20-2.112879/");
                }

                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static UpdateChecker getInstance() {
        if (instance == null) {
            instance = new UpdateChecker();
        }
        return instance;
    }
}
