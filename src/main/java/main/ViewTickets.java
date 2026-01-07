package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tickets.HistoryOfTicket;
import tickets.Ticket;
import users.Developer;
import users.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ViewTickets {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ArrayNode getVisibleTickets(User util, List<Ticket> allTickets) {
        ArrayNode array = mapper.createArrayNode();

        // aici trebuie sa vad ce afisez in functie de tipul de user
        if (util.getUsername().contains("manager")) {
            // afisez toate ticketele; managerii au voie sa le acceseze pe toate
            for (Ticket unTicket : allTickets) {
                array.add(ticketToJson(unTicket));
            }

        } else if (util.getUsername().contains("reporter")) {
            // nimic aici
            // mananc caca, aici se vad doar ticketele date de acel reporter;
            return array;

        } else {
            // am developer; afisez doar ticketele open care sunt
            // assignate aceluiasi milestone
            Developer dev = (Developer) util;
            for (Ticket unTicket : allTickets) {
                if(unTicket.getStatus().equals("OPEN") && dev.getMilName().contains(unTicket.getMilName())) {
                    array.add(ticketToJson(unTicket));
                }
            }
            return array;

        }
        // returnez ce tichete afisez
        return array;
    }

    public static ArrayNode getAssignedTickets(User util, List<Ticket> assigTickets) {
        ArrayNode array = mapper.createArrayNode();

        assigTickets.sort(Comparator
                .comparingInt((Ticket t) -> priorityValue(
                        t.getBusinessPriority() != null ? t.getBusinessPriority() : "LOW"
                ))
                .thenComparing(t -> {
                    String dateStr = t.getCreatedAt() != null ? t.getCreatedAt() : "2100-01-01";
                    return LocalDate.parse(dateStr);
                })
                .thenComparingInt(t -> t.getId())
        );

        for (Ticket unTicket : assigTickets) {
            array.add(ticketToJsonAssig(unTicket));
        }

        return array;
    }

    public static ArrayNode getHistoryTicket(User u, List<Ticket> allTickets) {
        ArrayNode result = mapper.createArrayNode();

        List<Ticket> visibleTickets = new ArrayList<>();

        // vad ce sunt mai intai
        if (u.getRole().equals("DEVELOPER")) {
            Developer dev = (Developer) u;

            // tichete asignate + cele la care a renunțat
            visibleTickets.addAll(dev.getAssignedTckets());
            visibleTickets.addAll(dev.getPastAssigTickets());
        } else if (u.getRole().equals("MANAGER")) {
            // managerul vede tichetele din milestone-urile lui
            for (Ticket t : allTickets) {
                if (t.getMilName().equals(u.getUsername())) {
                    visibleTickets.add(t);
                }
            }
        }

        // sortez tichetele
        visibleTickets.sort(Comparator.comparing(Ticket::getCreatedAt)
             .thenComparingInt(Ticket::getId));

        // dau cu afisarea
        for (Ticket t : visibleTickets) {
            ObjectNode ticketNode = mapper.createObjectNode();

            ticketNode.put("id", t.getId());
            ticketNode.put("title", t.getTitle());
            ticketNode.put("status", t.getStatus());

            // actiunile acum
            ArrayNode actionsArray = mapper.createArrayNode();

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

            ArrayNode commentsArray = mapper.createArrayNode();
            if (t.getComments() != null) {
                t.getComments().forEach(c -> {
                    ObjectNode cNode = mapper.createObjectNode();
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

    private static ObjectNode ticketToJsonAssig(Ticket t) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", t.getId());
        node.put("type", t.getType());
        node.put("title", t.getTitle());
        node.put("businessPriority", t.getBusinessPriority());
        node.put("status", t.getStatus());
        node.put("createdAt", t.getCreatedAt());
        node.put("assignedAt", t.getAssignedAt());
        node.put("reportedBy", t.getReportedBy());
        //   node.putArray("comments"); // momentan gol
        ArrayNode commentsArray = mapper.createArrayNode();

        if (t.getComments() != null) {
            for (tickets.Comment c : t.getComments()) {
                ObjectNode commentNode = mapper.createObjectNode();
                commentNode.put("author", c.getAuthor());
                commentNode.put("content", c.getMessage());
                commentNode.put("createdAt", c.getCreatedAt());
                commentsArray.add(commentNode);
            }
        }

        node.set("comments", commentsArray);
        return node;
    }

    private static ObjectNode historyToJson(HistoryOfTicket h) {
        ObjectNode node = mapper.createObjectNode();
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

    private static ObjectNode ticketToJson(Ticket t) {
        ObjectNode node = mapper.createObjectNode();
        node.put("id", t.getId());
        node.put("type", t.getType());
        node.put("title", t.getTitle());
        node.put("businessPriority", t.getBusinessPriority());
        node.put("status", t.getStatus());
        node.put("createdAt", t.getCreatedAt());
        node.put("assignedAt", t.getAssignedAt());
        node.put("solvedAt", t.getSolvedAt());
        node.put("assignedTo", t.getAssignedTo());
        node.put("reportedBy", t.getReportedBy());
        // node.putArray("comments"); // momentan gol
        ArrayNode commentsArray = mapper.createArrayNode();

        if (t.getComments() != null) {
            for (tickets.Comment c : t.getComments()) {
                ObjectNode commentNode = mapper.createObjectNode();
                commentNode.put("author", c.getAuthor());
                commentNode.put("content", c.getMessage());
                commentNode.put("createdAt", c.getCreatedAt());
                commentsArray.add(commentNode);
            }
        }

        node.set("comments", commentsArray);
        return node;
    }

    private static int priorityValue(String bussinesPriority) {
        switch (bussinesPriority) {
            case "CRITICAL": return 1;
            case "HIGH": return 2;
            case "MEDIUM": return 3;
            case "LOW": return 4;
            default: return 0;
        }
    }
}
