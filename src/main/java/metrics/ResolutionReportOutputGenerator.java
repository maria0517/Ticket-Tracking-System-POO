package metrics;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tickets.Ticket;

import java.util.List;

import static metrics.ResolutionEfficiencyCalcScore.calculateEfficiency;

public class ResolutionReportOutputGenerator {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectNode resolutionReportFormat (List<Ticket> ticks) {
        ObjectNode reportNode = mapper.createObjectNode();
        // acum trebuie sa generez raportul (il fac strict pentru afisare
        reportNode.put("totalTickets", ticks.size());

        // cate sunt din fiecare
        ObjectNode tickestByTypeNode = mapper.createObjectNode();
        tickestByTypeNode.put("BUG", ticks.stream().
                filter(t -> t.getType().equals("BUG")).count());
        tickestByTypeNode.put("FEATURE_REQUEST", ticks.stream().
                filter(t -> t.getType().equals("FEATURE_REQUEST")).count());
        tickestByTypeNode.put("UI_FEEDBACK", ticks.stream().
                filter(t -> t.getType().equals("UI_FEEDBACK")).count());

        reportNode.set("ticketsByType", tickestByTypeNode);

        // acum dupa prioritati
        ObjectNode tickestByPriorNode = mapper.createObjectNode();
        tickestByPriorNode.put("LOW", ticks.stream().filter(t ->
                t.getBusinessPriority().equals("LOW")).count());
        tickestByPriorNode.put("MEDIUM", ticks.stream().filter(t ->
                t.getBusinessPriority().equals("MEDIUM")).count());
        tickestByPriorNode.put("HIGH", ticks.stream().filter(t ->
                t.getBusinessPriority().equals("HIGH")).count());
        tickestByPriorNode.put("CRITICAL", ticks.stream().filter(t ->
                t.getBusinessPriority().equals("CRITICAL")).count());
        reportNode.set("ticketsByPriority", tickestByPriorNode);

        double scorBug = 0;
        double scorReq = 0;
        double scorUIFeed = 0;
        for (Ticket tick : ticks) {
            // System.out.println("ticketul cu " + tick.getId() + " are statusul: " + tick.getBusinessPriority());
            if (tick.getType().equals("BUG")) {
                scorBug += calculateEfficiency(tick);
            } else if (tick.getType().equals("FEATURE_REQUEST")) {
                scorReq += calculateEfficiency(tick);
            } else {
                scorUIFeed += calculateEfficiency(tick);
            }
        }

        // astea apoi trebuie impartite la nr tichete si rotunjite
        scorBug = Math.round(scorBug / (double) ticks.stream().
                filter(t -> t.getType().equals("BUG")).count() * 100.0) / 100.0;
        scorReq = Math.round(scorReq / (double) ticks.stream().filter(t -> t.getType()
                .equals("FEATURE_REQUEST")).count() * 100.0) / 100.0;
        scorUIFeed = Math.round(scorUIFeed / (double) ticks.stream().filter(t -> t.getType()
                .equals("UI_FEEDBACK")).count() * 100.0) / 100.0;
        ObjectNode typeNode = mapper.createObjectNode();
        typeNode.put("BUG", scorBug);
        typeNode.put("FEATURE_REQUEST", scorReq);
        typeNode.put("UI_FEEDBACK", scorUIFeed);
        reportNode.set("efficiencyByType", typeNode);

        // gata
        return reportNode;
    }
}
