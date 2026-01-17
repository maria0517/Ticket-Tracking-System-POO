package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tickets.Ticket;
import users.Developer;

import java.util.List;

public final class ViewAfterSearch {

    private ViewAfterSearch() { }
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * creeaza tot outputul pentru tichete
     */
    public static ArrayNode viewSearchedTickets(final List<Ticket> tickets,
           final JsonNode filtersNode, final boolean manComm) {
        ArrayNode ticketsNode = MAPPER.createArrayNode();
        List<String> keywords = null;
        for (Ticket tick : tickets) {
            ticketsNode.add(searchedTickToJson(tick, filtersNode, manComm));
        }
        return ticketsNode;
    }

    /**
     * output search de developeri
     */
    public static ArrayNode viewSearchedDevs(final List<Developer> devs) {
        ArrayNode devsNode = MAPPER.createArrayNode();
        for (Developer dev : devs) {
            devsNode.add(searchedDEvToJson(dev));
        }
        return devsNode;
    }

    /**
     * imi face formatul de tichet pentru output de la search
     */
    public static ObjectNode searchedTickToJson(final Ticket t, final JsonNode filterNode,
                final boolean manComm) {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("id", t.getId());
        node.put("type", t.getType());
        node.put("title", t.getTitle());
        node.put("businessPriority", t.getBusinessPriority());
        node.put("status", t.getStatus());
        node.put("createdAt", t.getCreatedAt());
        node.put("solvedAt", t.getSolvedAt());
        node.put("reportedBy", t.getReportedBy());
        if (filterNode.has("keywords")) {
            node.put("matchingWords", filterNode.get("keywords"));
        } else if (manComm) {
            // inseamna ca am manager si il dau efectiv gol, ce ciudatenie mare
            ArrayNode filtersNode = MAPPER.createArrayNode();
            node.put("matchingWords", filtersNode);
        }
        return node;
    }

    /**
     * imi face formatul de output pentru developer
     */
    public static ObjectNode searchedDEvToJson(final Developer dev) {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("username", dev.getUsername());
        node.put("expertiseArea", dev.getExpertiseArea());
        node.put("seniority", dev.getSeniority());
        node.put("performanceScore", dev.getPerforScore());
        node.put("hireDate", dev.getHireDate());
        return node;
    }
}
