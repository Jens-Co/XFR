package dev.kejonamc.options;

import dev.kejonamc.chewbotcca.RestClient;
import dev.kejonamc.configuration.Configurate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.HashMap;

public class FriendsList {
    private final Configurate config;
    private final HashMap<String, String> friendsHashMap = new HashMap<>();
    private final Logger logger;

    public FriendsList(Configurate config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }

    public void friendsUpdater() {
        JSONObject response;
        try {
            response = new JSONObject(RestClient.getXBL("https://xbl.io/api/v2/friends?xuid=" + config.getXUID(), config.getApiKey()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JSONArray data = (JSONArray) response.get("people");
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject2 = (JSONObject) data.get(i);
            friendsHashMap.put((String) jsonObject2.get("xuid"), (String) jsonObject2.get("displayName"));
        }
        response.clear();
        logger.info("friends list has updated.");
    }

    public HashMap<String, String> getFriendsHashMap() {
        return friendsHashMap;
    }
}
