package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import users.Developer;

public final class ViewNotifications {

    private ViewNotifications() { }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * @return la toate notificarile unui anumit dev
     */
    public static ArrayNode getNotifications(final Developer dev) {
        ArrayNode notifArray = MAPPER.createArrayNode();

        for (String msg : dev.getNotifications()) {
            notifArray.add(msg);
        }
        // sterg tot
        dev.clearNotifications();
        return notifArray;
    }
}
