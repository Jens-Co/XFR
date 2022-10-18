package dev.kejonamc.options;

import dev.kejonamc.XFR;
import dev.kejonamc.chewbotcca.RestClient;
import dev.kejonamc.configuration.Configurate;
import dev.kejonamc.database.DatabaseQuery;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Purge {
    private final Logger logger;
    private final XFR INSTANCE;
    private final Configurate config;

    public Purge(Logger logger, XFR instance, Configurate config) {
        this.logger = logger;
        this.INSTANCE = instance;
        this.config = config;

        // Auto purge friends
        if (config.getAutoPurge()) {
            logger.info("Auto Purge enabled.");
            autoPurge();
        }
    }

    public void autoPurge() {
        int delay = 5000; // delay for 5 sec.
        int period = 1000 * 60 * 60 * config.getTime();
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
        if (INSTANCE.playerDatabase.isEmpty()) {
            logger.info("database hashmap was empty");
            return;
        }
        // Date calculation.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -10);
        // Current date minus 10 days.
        for (String xuid : INSTANCE.playerDatabase.keySet()) {
            if (INSTANCE.playerDatabase.get(xuid).before(cal.getTime())) {
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
            INSTANCE.playerDatabase = databaseQuery.databaseMap();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println(INSTANCE.playerDatabase);
    }
}
