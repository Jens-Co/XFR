package dev.kejonamc;

import dev.kejonamc.chewbotcca.RestClient;
import dev.kejonamc.configuration.Configurate;
import dev.kejonamc.database.DatabaseQuery;
import dev.kejonamc.database.DatabaseSetup;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

public class XFR {
    private static final Logger logger = LoggerFactory.getLogger(XFR.class);
    private static Configurate config = null;
    private HashMap<String, Date> playerDatabase;

    public static void main(String[] args) throws IOException {
        // Generate Config file
        Path path = Paths.get("");
        try {
            config = Configurate.configuration(path.toAbsolutePath());
        } catch (IOException e) {
            logger.error("Could not create config.yml! " + e.getMessage());
            return;
        }
        // Enable database
        if (config.getEnableDatabase()) {
            new DatabaseSetup().mysqlSetup(config, logger);
        } else {
            logger.error("Database was not enabled. Deactivating XFR");
            return;
        }
        // Auto purge friends
        if (config.getAutoPurge()) {
            new XFR().autoPurge();
        }

        logger.info("has enabled.");
        // Read console for commands.
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        switch (reader.readLine()) {
            case "stop" -> System.exit(0);
            case "purge" -> new XFR().purgeFriends();
            default -> logger.warn("wrong command: " + reader.readLine());
        }
    }

    public void autoPurge() {
        int delay = 5000; // delay for 5 sec.
        int period = 1000 * 60 * 60; // repeat every hour.
        Timer timer = new Timer();
        logger.info("Auto purging friends");

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                purgeFriends();
            }
        }, delay, period);
    }

    public void purgeFriends() {
        // Make sure hashmap is updated.
        updateDatabaseHashmap();
        if (playerDatabase.isEmpty()) {
            logger.info("database hashmap was empty");
            return;
        }
        // Date calculation.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -10);
        // Current date minus 10 days.
        for (String xuid : playerDatabase.keySet()) {
            if (playerDatabase.get(xuid).before(cal.getTime())) {
                JSONObject response = null;
                // Remove friend from XboxLive
                try {
                    response = new JSONObject(RestClient.getXBL("https://xbl.io/api/v2/friends/remove/" + xuid, config.getApiKey()));
                } catch (JSONException ignored) {
                }
                assert response != null;
                response.clear();
            }
        }
    }

    public void updateDatabaseHashmap() {
        DatabaseQuery databaseQuery = new DatabaseQuery();
        try {
            playerDatabase = databaseQuery.databaseMap();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(playerDatabase);
    }
}
