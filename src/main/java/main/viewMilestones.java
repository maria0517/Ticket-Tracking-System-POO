package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.jdi.ArrayReference;
import tickets.Ticket;
import users.Developer;
import users.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class viewMilestones {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ArrayNode viewGoodMilestones(String username, List<User> allUsers, List<Milestone> allMilestones, String timestamp) {
        ArrayNode array =  mapper.createArrayNode();
        List<Milestone> visible = new ArrayList<>();

        for (Milestone m : allMilestones) {
            if (m.getCreatorName().equals(username) || m.getAssignedDevs().contains(username)) {
                visible.add(m);
            }
        }

        // fac sortarea
        visible.sort(Comparator.comparing(Milestone::getDueDate)
            .thenComparing(Milestone::getName));

        // acum trebuie creat efectiv outputul

        for (Milestone m : visible) {
            ObjectNode node = mapper.createObjectNode();

            node.put("name", m.getName());
            ArrayNode blockingForNode = mapper.createArrayNode();
            for (String otherMil : m.getBlockingFor()) {
                blockingForNode.add(otherMil);
            }
            node.set("blockingFor", blockingForNode);
            node.put("dueDate", m.getDueDate().toString());
            node.put("createdAt", m.getCreatedAt());
            ArrayNode TicketsNode = mapper.createArrayNode();
            for (Integer i : m.getTickets()) {
                TicketsNode.add(i);
            }
            node.set("tickets", TicketsNode);
            ArrayNode devsNode = mapper.createArrayNode();
            for (String devName : m.getAssignedDevs()) {
                devsNode.add(devName);
            }
            node.set("assignedDevs", devsNode);
            node.put("createdBy", m.getCreatorName());
            node.put("status", m.getStatus());
            node.put("isBlocked", m.isBlocked());
            node.put("daysUntilDue", m.getDaysUntilDue(timestamp));
            node.put("overdueBy", m.getOverdue());
            ArrayNode openTicketsNode = mapper.createArrayNode();
            // sortez pt afisare
            Collections.sort(m.getOpenTickets());
            for (Integer id : m.getOpenTickets()) {
                openTicketsNode.add(id);
            }
            node.set("openTickets", openTicketsNode);
            ArrayNode closedTicketsNode = mapper.createArrayNode();
            Collections.sort(m.getClosedTickets());
            for (Integer id : m.getClosedTickets()) {
                closedTicketsNode.add(id);
            }
            node.set("closedTickets", closedTicketsNode);
            node.put("completionPercentage", m.getCompletPerc());

            // repartition aici
            ArrayNode repartitionNode = mapper.createArrayNode();
            for (String dev : m.getAssignedDevs()) {
                ObjectNode devNode = mapper.createObjectNode();

                devNode.put("developer", dev);

                for (User u : allUsers) {
                    if (u.getUsername().equals(dev)) {
                        // am gasit dev ii iau toate ticketele assigned
                        Developer developer = (Developer) u;
                        if (developer.getAssignedTckets().size() == 0) {
                            ArrayNode emptyTickets = mapper.createArrayNode();
                            devNode.set("assignedTickets", emptyTickets);
                        } else {
                            // am
                            ArrayNode assigTick = mapper.createArrayNode();
                            // le sortez pentru afisare strict
                            // pt logica nu conteaza
                            developer.getAssignedTckets()
                                    .sort(Comparator.comparingInt(Ticket::getId));
                            for (Ticket t : developer.getAssignedTckets()) {
                                if (t.getMilName().equals(m.getName())) {
                                    assigTick.add(t.getId());
                                }
                            }
                            devNode.set("assignedTickets", assigTick);
                        }
                    }
                }
                repartitionNode.add(devNode);
            }

            node.set("repartition", repartitionNode);

            array.add(node);
        }

        return array;
    }
}
