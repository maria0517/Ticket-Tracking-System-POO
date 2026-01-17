package milestones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tickets.Ticket;
import users.Developer;
import users.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ViewMilestones {

    private ViewMilestones() { }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * functie pentru afisare milestones
     */
    public static ArrayNode viewGoodMilestones(final String username, final List<User> allUsers,
                                     final List<Milestone> allMilestones, final String timestamp) {
        ArrayNode array =  MAPPER.createArrayNode();
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
            ObjectNode node = MAPPER.createObjectNode();

            node.put("name", m.getName());
            ArrayNode blockingForNode = MAPPER.createArrayNode();
            for (String otherMil : m.getBlockingFor()) {
                blockingForNode.add(otherMil);
            }
            node.set("blockingFor", blockingForNode);
            node.put("dueDate", m.getDueDate().toString());
            node.put("createdAt", m.getCreatedAt());
            ArrayNode ticketsNode = MAPPER.createArrayNode();
            for (Integer i : m.getTickets()) {
                ticketsNode.add(i);
            }
            node.set("tickets", ticketsNode);
            ArrayNode devsNode = MAPPER.createArrayNode();
            for (String devName : m.getAssignedDevs()) {
                devsNode.add(devName);
            }
            node.set("assignedDevs", devsNode);
            node.put("createdBy", m.getCreatorName());
            node.put("status", m.getStatus());
            node.put("isBlocked", m.isBlocked());
            node.put("daysUntilDue", m.getDaysUntilDue(timestamp));
            node.put("overdueBy", m.getOverdue());
            ArrayNode openTicketsNode = MAPPER.createArrayNode();
            // sortez pt afisare
            Collections.sort(m.getOpenTickets());
            for (Integer id : m.getOpenTickets()) {
                openTicketsNode.add(id);
            }
            node.set("openTickets", openTicketsNode);
            ArrayNode closedTicketsNode = MAPPER.createArrayNode();
            Collections.sort(m.getClosedTickets());
            for (Integer id : m.getClosedTickets()) {
                closedTicketsNode.add(id);
            }
            node.set("closedTickets", closedTicketsNode);
            node.put("completionPercentage", m.getCompletPerc());

            // repartition aici
            ArrayNode repartitionNode = MAPPER.createArrayNode();
            for (String dev : m.getAssignedDevs()) {
                ObjectNode devNode = MAPPER.createObjectNode();

                devNode.put("developer", dev);

                for (User u : allUsers) {
                    if (u.getUsername().equals(dev)) {
                        // am gasit dev ii iau toate ticketele assigned
                        Developer developer = (Developer) u;
                        if (developer.getAssignedTckets().size() == 0) {
                            ArrayNode emptyTickets = MAPPER.createArrayNode();
                            devNode.set("assignedTickets", emptyTickets);
                        } else {
                            // am
                            ArrayNode assigTick = MAPPER.createArrayNode();
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
        // gata
        return array;
    }
}
