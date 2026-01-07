package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import users.Developer;

public class viewNotifications {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ArrayNode getNotifications(Developer dev) {
        ArrayNode notifArray = mapper.createArrayNode();

        for (String msg : dev.getNotifications()) {
            notifArray.add(msg);
        }
        // sterg tot
        dev.clearNotifications();
        return notifArray;
    }
}
