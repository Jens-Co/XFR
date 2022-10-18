package dev.kejonamc.options;

import dev.kejonamc.chewbotcca.RestClient;
import dev.kejonamc.configuration.Configurate;
import org.slf4j.Logger;

import java.util.*;

public class Purge {
    private final Logger logger;
    private final Configurate config;
    private final LastSeenFriends lastSeenFriends;
    private final FriendsList friendsList;

    public Purge(Logger logger, Configurate config) {
        this.logger = logger;
        this.config = config;
        friendsList = new FriendsList(config, logger);
        lastSeenFriends = new LastSeenFriends(config, logger);

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
        //Update both hashmaps.
        lastSeenFriends.lastSeenFriendsUpdater();
        friendsList.friendsUpdater();

        if (lastSeenFriends.getLastSeenFriendsHashMap().isEmpty()) {
            logger.info("Last seen friends hashmap was empty");
            return;
        }

        if (friendsList.getFriendsHashMap().isEmpty()) {
            logger.info("Last seen friends hashmap was empty");
            return;
        }
        // Date calculation.
        Calendar cal = Calendar.getInstance();
        // Current date minus 10 days.
        cal.add(Calendar.MINUTE, -10);
        // Loop all last seen friends.
        for (String lastSeenFriendsXUID : lastSeenFriends.getLastSeenFriendsHashMap().keySet()) {
            // Check if last seen friend is actually a friend.
            if (friendsList.getFriendsHashMap().containsKey(lastSeenFriendsXUID)) {
                // Check if friend is over 10 days friend.
                if (lastSeenFriends.getLastSeenFriendsHashMap().get(lastSeenFriendsXUID).before(cal.getTime())) {
                    // Remove friend from XboxLive
                    try {
                        int serverCode = Integer.parseInt(RestClient.xblStatusCode("https://xbl.io/api/v2/friends/remove/" + lastSeenFriendsXUID, config.getApiKey()));
                        if (serverCode == 200) {
                            logger.info("Removed account: " + lastSeenFriendsXUID + " : " + lastSeenFriends.getLastSeenFriendsHashMap().get(lastSeenFriendsXUID));
                            lastSeenFriends.getLastSeenFriendsHashMap().remove(lastSeenFriendsXUID);
                        } else {
                            logger.warn("Could not remove account: " + lastSeenFriendsXUID + " : " + lastSeenFriends.getLastSeenFriendsHashMap().get(lastSeenFriendsXUID));
                        }
                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    logger.info("player only joined on " + lastSeenFriends.getLastSeenFriendsHashMap().get(lastSeenFriendsXUID));
                }
            } else {
                logger.info("All friends recently joined or all older friends have been removed!");
            }
        }
    }
}
