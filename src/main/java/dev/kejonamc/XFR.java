package dev.kejonamc;

import dev.kejonamc.configuration.Configurate;
import dev.kejonamc.gui.XFRGui;
import dev.kejonamc.options.FriendsList;
import dev.kejonamc.options.Purge;
import dev.kejonamc.util.TextAreaOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class XFR {
    private static Logger logger;
    private static Configurate config = null;
    private XFRGui gui = null;
    public static void main(String[] args)  {
        // Enable plugin.
        XFR enable = new XFR();
        enable.onEnable();
        // Loop console input for commands.
        int num = 1;
        while (num > 0) {
            try {
                enable.consoleInput();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void onEnable(){
        // Check if there is a log file. move previous log file and create a new.
        Path path = Paths.get("").toAbsolutePath();
        File logfile = new File(path + "/logs/latest.log");
        if (logfile.exists()) {
            try {
                logfile.renameTo(new File(path + "/logs/" + LocalDateTime.now().toString().replace(":", "-") + ".log"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        logger = LoggerFactory.getLogger(XFR.class);

        try {
            config = Configurate.configuration(path);
        } catch (IOException e) {
            logger.error("Could not create config.yml! " + e.getMessage());
            onDisable();
        }

        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            Thread thread = new Thread(() -> {
                gui = new XFRGui();
                logFileReader();
            });
            thread.start();
        }

        new Purge();
        logger.info("XFR has enabled.");

    }

    public void onDisable() {
        logger.info("Shutting down!");
        System.exit(0);
    }

    public void consoleInput() throws IOException {
              // Read console for commands.
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        switch (reader.readLine() ) {
            case "stop" -> onDisable();
            case "purge" -> new Purge().purgeFriends();
            case "friends" -> {
                FriendsList friendsList = new FriendsList();
                friendsList.friendsUpdater();
            }
            default -> logger.warn("wrong command: " + reader.readLine());
        }
    }

    public void logFileReader() {
        try {
            Path path = Paths.get("").toAbsolutePath();
            FileInputStream fstream = new FileInputStream(
                    path + "/logs/latest.log");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String line;
            while (true) {
                line = br.readLine();
                if (line == null) {
                    Thread.sleep(500);
                } else {
                    PrintStream con = new PrintStream(new TextAreaOutputStream(gui.textArea));
                    con.println(line);
                }
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public Configurate getConfig() {
        return config;
    }
    public Logger getLogger() {
        return logger;
    }
}
