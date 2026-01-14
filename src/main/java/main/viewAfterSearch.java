package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tickets.Ticket;
import users.Developer;

import java.util.List;

public class viewAfterSearch {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ArrayNode viewSearchedTickets(List<Ticket> tickets, JsonNode filtersNode, boolean manComm) {
        ArrayNode ticketsNode = mapper.createArrayNode();
        List<String> keywords = null;
        for (Ticket tick : tickets) {
            ticketsNode.add(searchedTickToJson(tick, filtersNode, manComm));
        }
        return ticketsNode;
    }

    public static ArrayNode viewSearchedDevs(List<Developer> devs) {
        ArrayNode devsNode = mapper.createArrayNode();
        for (Developer dev : devs) {
            devsNode.add(searchedDEvToJson(dev));
        }
        return devsNode;
    }

    public static ObjectNode searchedTickToJson(Ticket t, JsonNode filterNode, boolean manComm) {
        ObjectNode node = mapper.createObjectNode();
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
            ArrayNode filtersNode = mapper.createArrayNode();
            node.put("matchingWords", filtersNode);
        }
        return node;
    }

    public static ObjectNode searchedDEvToJson(Developer dev) {
        ObjectNode node = mapper.createObjectNode();
        node.put("username", dev.getUsername());
        node.put("expertiseArea", dev.getExpertiseArea());
        node.put("seniority", dev.getSeniority());
        node.put("performanceScore", 0.0); // momentan
        node.put("hireDate", dev.getHireDate());
        return node;
    }
}
