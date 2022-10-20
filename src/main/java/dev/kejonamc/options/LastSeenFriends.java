package dev.kejonamc.options;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kejonamc.XFR;
import dev.kejonamc.chewbotcca.RestClient;
import dev.kejonamc.configuration.Configurate;
import dev.kejonamc.json.Person;
import dev.kejonamc.json.Root;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class LastSeenFriends {

    private final Logger logger = new XFR().getLogger();
    private final Configurate config = new XFR().getConfig();
    private final HashMap<String, Date> lastSeenFriendsHashMap = new HashMap<>();

    void lastSeenFriendsUpdater() {
        logger.info("Getting the dates of recent players!");
        JSONObject response;
        try {
            response = new JSONObject(RestClient.getXBL("https://xbl.io/api/v2/recent-players", config.getApiKey()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        try {
            ObjectMapper om = new ObjectMapper();
            Root root = om.readValue(response.toString(), Root.class);
            ArrayList<Person> allLastSeenFriends = root.getPeople();
            for (Person lastSeenFriend : allLastSeenFriends) {
                lastSeenFriendsHashMap.put(lastSeenFriend.getXuid(), lastSeenFriend.getRecentPlayer().getTitles().get(0).getLastPlayedWithDateTime());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        response.clear();
        logger.info("Last seen friends list has updated.");
    }

    public HashMap<String, Date> getLastSeenFriendsHashMap() {
        return lastSeenFriendsHashMap;
    }
}
