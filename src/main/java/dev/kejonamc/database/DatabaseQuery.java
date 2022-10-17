package dev.kejonamc.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;

public class DatabaseQuery {
    // Query database for player balance

    public HashMap<String, Date> databaseMap() throws SQLException {
        HashMap<String, Date> dbMap = new HashMap<>();
            PreparedStatement statement = DatabaseSetup.getConnection()
                    .prepareStatement("SELECT * FROM " + DatabaseSetup.table + " XUID");
            ResultSet results = statement.executeQuery();
            results.next();
            dbMap.put(results.getString("XUID"), results.getDate("LASTLOGIN"));
        return dbMap;
    }
}
