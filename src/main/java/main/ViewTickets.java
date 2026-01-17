package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import milestones.Milestone;
import tickets.HistoryOfTicket;
import tickets.Ticket;
import users.Developer;
import users.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public final class ViewTickets {

    private ViewTickets() { }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * scoate outputul pentru tichete
     */
    public static ArrayNode getVisibleTickets(final User util, final List<Ticket> allTickets) {
        ArrayNode array = MAPPER.createArrayNode();

        // aici trebuie sa vad ce afisez in functie de tipul de user
        if (util.getUsername().contains("manager")) {
            // afisez toate ticketele; managerii au voie sa le acceseze pe toate
            for (Ticket unTicket : allTickets) {
                array.add(ticketToJson(unTicket));
            }
        } else if (util.getUsername().contains("reporter")) {
            // nimic aici
            // mananc caca, aici se vad doar ticketele date de acel reporter
            return array;
        } else {
            // am developer; afisez doar ticketele open care sunt
            // assignate aceluiasi milestone
            Developer dev = (Developer) util;
            for (Ticket unTicket : allTickets) {
                if (unTicket.getStatus().equals("OPEN")
                        && dev.getMilName().contains(unTicket.getMilName())) {
                    array.add(ticketToJson(unTicket));
                }
            }
            return array;

        }
        // returnez ce tichete afisez
        return array;
    }

    /**
     * returneaza doar outputul pentru tichetele assignate
     */
    public static ArrayNode getAssignedTickets(final List<Ticket> assigTickets) {
        ArrayNode array = MAPPER.createArrayNode();

        assigTickets.sort(Comparator.comparingInt((Ticket t) -> priorityValue(
                t.getBusinessPriority() != null ? t.getBusinessPriority() : "LOW"))
                .thenComparing(t -> {
                String dateStr = t.getCreatedAt() != null
                // aici am pus o data de nu o sa ajunga niciodata
                ? t.getCreatedAt() : "2100-01-01"; return LocalDate.parse(dateStr); })
                .thenComparingInt(t -> t.getId()));

        for (Ticket unTicket : assigTickets) {
            array.add(ticketToJsonAssig(unTicket));
        }
        return array;
    }

    /**
     * imi aduce istoricul unui tichet
     */
    public static ArrayNode getHistoryTicket(final User u, final List<Ticket> allTickets,
        final Map<String, Milestone> allMilestones) {
        ArrayNode result = MAPPER.createArrayNode();

        List<Ticket> visibleTickets = new ArrayList<>();

        // vad ce sunt mai intai
        if (u.getRole().equals("DEVELOPER")) {
            Developer dev = (Developer) u;

            // tichete asignate + cele la care a renuntat
            visibleTickets.addAll(dev.getAssignedTckets());
            visibleTickets.addAll(dev.getPastAssigTickets());
        } else if (u.getRole().equals("MANAGER")) {
            // managerul vede tichetele din milestoneurile lui
            for (Milestone mil :  allMilestones.values()) {
                if (mil.getCreatorName().equals(u.getUsername())) {
                    // am un milestone facut de acest manager; iau toate ticketele din el
                    for (Integer idTick : mil.getTickets()) {
                        visibleTickets.add(allTickets.get(idTick));
                    }
                }
            }
        }

        // sortez tichetele
        visibleTickets.sort(Comparator.comparing(Ticket::getCreatedAt)
             .thenComparingInt(Ticket::getId));

        // dau cu afisarea
        for (Ticket t : visibleTickets) {
            ObjectNode ticketNode = MAPPER.createObjectNode();

            ticketNode.put("id", t.getId());
            ticketNode.put("title", t.getTitle());
            ticketNode.put("status", t.getStatus());

            // actiunile acum
            ArrayNode actionsArray = MAPPER.createArrayNode();

            for (HistoryOfTicket h : t.getHistory()) {

                // daca dev a dat undoAssign → STOP
                if (u.getRole().equals("DEVELOPER")
                        && h.getAction().equals("DE-ASSIGNED")
                        && h.getAuthor().equals(u.getUsername())) {
                    actionsArray.add(historyToJson(h));
                    break;
                }

                actionsArray.add(historyToJson(h));
            }

            ticketNode.set("actions", actionsArray);

            ArrayNode commentsArray = MAPPER.createArrayNode();
            if (t.getComments() != null) {
                t.getComments().forEach(c -> {
                    ObjectNode cNode = MAPPER.createObjectNode();
                    cNode.put("author", c.getAuthor());
                    cNode.put("content", c.getMessage());
                    cNode.put("createdAt", c.getCreatedAt());
                    commentsArray.add(cNode);
                });
            }

            ticketNode.set("comments", commentsArray);

            result.add(ticketNode);
        }

        return result;
    }

    /**
     * formatare ticket asignat
     */
    private static ObjectNode ticketToJsonAssig(final Ticket t) {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("id", t.getId());
        node.put("type", t.getType());
        node.put("title", t.getTitle());
        node.put("businessPriority", t.getBusinessPriority());
        node.put("status", t.getStatus());
        node.put("createdAt", t.getCreatedAt());
        node.put("assignedAt", t.getAssignedAt());
        node.put("reportedBy", t.getReportedBy());
        //   node.putArray("comments"); // momentan gol
        ArrayNode commentsArray = MAPPER.createArrayNode();

        if (t.getComments() != null) {
            for (tickets.Comment c : t.getComments()) {
                ObjectNode commentNode = MAPPER.createObjectNode();
                commentNode.put("author", c.getAuthor());
                commentNode.put("content", c.getMessage());
                commentNode.put("createdAt", c.getCreatedAt());
                commentsArray.add(commentNode);
            }
        }

        node.set("comments", commentsArray);
        return node;
    }

    /**
     * formatare output pt istoricul unui tichet
     */
    private static ObjectNode historyToJson(final HistoryOfTicket h) {
        ObjectNode node = MAPPER.createObjectNode();
        if (h.getMilName() != null) {
            node.put("milestone", h.getMilName());
        }
        if (h.getFromStatus() != null) {
            node.put("from", h.getFromStatus());
        }
        if (h.getToStatus() != null) {
            node.put("to", h.getToStatus());
        }
        node.put("by", h.getAuthor());
        node.put("timestamp", h.getTimestamp());
        node.put("action", h.getAction());
        return node;
    }

    /**
     * parsare tichet in json pentru a l pune in output
     */
    private static ObjectNode ticketToJson(final Ticket t) {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("id", t.getId());
        node.put("type", t.getType());
        node.put("title", t.getTitle());
        node.put("businessPriority", t.getBusinessPriority());
        node.put("status", t.getStatus());
        node.put("createdAt", t.getCreatedAt());
        node.put("assignedAt", t.getAssignedAt());
        node.put("solvedAt", t.getSolvedAt());
        node.put("assignedTo", "");
        node.put("reportedBy", t.getReportedBy());
        // node.putArray("comments"); // momentan gol
        ArrayNode commentsArray = MAPPER.createArrayNode();

        if (t.getComments() != null) {
            for (tickets.Comment c : t.getComments()) {
                ObjectNode commentNode = MAPPER.createObjectNode();
                commentNode.put("author", c.getAuthor());
                commentNode.put("content", c.getMessage());
                commentNode.put("createdAt", c.getCreatedAt());
                commentsArray.add(commentNode);
            }
        }

        node.set("comments", commentsArray);
        return node;
    }

    /**
     * pentru cautarea dupa prioritate
     */
    private static int priorityValue(final String bussinesPriority) {
        switch (bussinesPriority) {
            case "CRITICAL": return 1;
            case "HIGH": return 2;
            case "MEDIUM": return 3;
            case "LOW": return 4;
            default: return 0;
        }
    }
}
