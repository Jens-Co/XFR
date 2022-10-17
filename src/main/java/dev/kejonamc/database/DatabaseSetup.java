package dev.kejonamc.database;

import dev.kejonamc.configuration.Configurate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.*;

public class DatabaseSetup {

    private static Connection connection = null;
    public static String table;

    public void mysqlSetup(@NotNull Configurate config, @NotNull Logger logger) {
        table = config.getTable();

        logger.info("Connecting to database...");

        File dataFolder = new File(Path.of(config.getPath()).toUri());
        if (!dataFolder.exists()) {
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            Statement createTables = connection.createStatement();
            createTables.executeUpdate("CREATE TABLE IF NOT EXISTS " + DatabaseSetup.table + " (XUID varchar(16), LASTLOGIN char(36))");
            createTables.close();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}