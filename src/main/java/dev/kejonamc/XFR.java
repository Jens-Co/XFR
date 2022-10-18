package dev.kejonamc;

import dev.kejonamc.configuration.Configurate;
import dev.kejonamc.database.DatabaseSetup;
import dev.kejonamc.options.FriendsList;
import dev.kejonamc.options.Purge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class XFR extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(XFR.class);
    private static Configurate config = null;
    public HashMap<String, Date> playerDatabase;
    public static void main(String[] args) {
        XFR enable = new XFR();
        enable.onEnable();
        int num = 1;
        while(num > 0) {
            try {
                enable.consoleReader();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onEnable() {
        // Generate Config file
        Path path = Paths.get("").toAbsolutePath();
        try {
            config = Configurate.configuration(path);
        } catch (IOException e) {
            logger.error("Could not create config.yml! " + e.getMessage());
            onDisable();
        }
        // Enable database
        if (config.getEnableDatabase()) {
            new DatabaseSetup().mysqlSetup(config, logger);
        } else {
            logger.error("Database was not enabled.");
        }

        new Purge(logger,this, config);
        logger.info("XFR has enabled.");
    }
    public void onDisable() {
        System.exit(0);
    }

    public void consoleReader() throws IOException {
        // Read console for commands.
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        switch (reader.readLine()) {
            case "stop" -> onDisable();
            case "purge" -> new Purge(logger, this, config).purgeFriends();
            case "friends" -> new FriendsList(config, logger).List();
            default -> logger.warn("wrong command: " + reader.readLine());
        }
    }
}
