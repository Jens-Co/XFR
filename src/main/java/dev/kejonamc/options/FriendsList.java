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
    private final HashMap<String, String> friendsMap = new HashMap<>();
    private final Logger logger;

    public FriendsList(Configurate config, Logger logger) {
        this.config = config;
        this.logger = logger;
    }
    public void List() {
        JSONObject response;
        // Remove friend from XboxLive
        try {
            response = new JSONObject(RestClient.getXBL("https://xbl.io/api/v2/friends?xuid=" + config.getXUID(), config.getApiKey()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        JSONArray data = (JSONArray) response.get("people");
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject2 = (JSONObject) data.get(i);
            String id = (String) jsonObject2.get("xuid");
            String name = (String) jsonObject2.get("displayName");
            friendsMap.put(id, name);

        }
        friendsMap.forEach((key, value) -> logger.info("xuid: " + key + " | " + "username: " + value));
        friendsMap.clear();
        response.clear();
    }
}
