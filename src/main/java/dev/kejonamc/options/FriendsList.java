package dev.kejonamc.options;

import dev.kejonamc.XFR;
import dev.kejonamc.chewbotcca.RestClient;
import dev.kejonamc.configuration.Configurate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.util.HashMap;

public class FriendsList {
    private final Logger logger = new XFR().getLogger();
    private final Configurate config = new XFR().getConfig();
    private final HashMap<String, String> friendsHashMap = new HashMap<>();

    public void friendsUpdater() {
        logger.info("Pulling latest friends from XboxLive");
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
            logger.info("Friend: " + jsonObject2.get("displayName") + "   --   XUID: " + jsonObject2.get("xuid"));
        }
        response.clear();
        logger.info("Friends list has updated.");
    }

    public HashMap<String, String> getFriendsHashMap() {
        return friendsHashMap;
    }
}
