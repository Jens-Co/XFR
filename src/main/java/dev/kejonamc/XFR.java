package dev.kejonamc;

import dev.kejonamc.configuration.Configurate;
import dev.kejonamc.options.Purge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class XFR {
    private static final Logger logger = LoggerFactory.getLogger(XFR.class);
    private static Configurate config = null;

    public static void main(String[] args) {
        XFR enable = new XFR();
        enable.onEnable();
        int num = 1;
        while (num > 0) {
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

        new Purge(logger, config);
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
            case "purge" -> new Purge(logger, config).purgeFriends();
            default -> logger.warn("wrong command: " + reader.readLine());
        }
    }
}
