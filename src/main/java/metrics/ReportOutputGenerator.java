package metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tickets.Ticket;

import java.util.List;

import static metrics.CustomerImpCalcScore.calculateImpact;
import static metrics.MetricsConst.getRiskInterv;
import static metrics.TicketRiskCalcScore.calculateRisk;

public class ReportOutputGenerator {

    private static final ObjectMapper mapper = new ObjectMapper();


    public static ObjectNode ReportFormat (List<Ticket> ticks, String command) {
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
        // dupa vin calculele puternice

        double scorBug = 0;
        double scorReq = 0;
        double scorUIFeed = 0;
        for (Ticket tick : ticks) {
            if (command.equals("generateCustomerImpactReport")) {
                if (tick.getType().equals("BUG")) {
                    scorBug += calculateImpact(tick);
                } else if (tick.getType().equals("FEATURE_REQUEST")) {
                    scorReq += calculateImpact(tick);
                } else {
                    scorUIFeed += calculateImpact(tick);
                }
            } else if (command.equals("generateTicketRiskReport")) {
                if (tick.getType().equals("BUG")) {
                    scorBug += calculateRisk(tick);
                } else if (tick.getType().equals("FEATURE_REQUEST")) {
                    scorReq += calculateRisk(tick);
                } else {
                    scorUIFeed += calculateRisk(tick);
                }
            }

        }
        // am scorurile
        // le impart la nr de tick specifice si afisez
        scorBug = Math.round(scorBug / (double) ticks.stream().
                filter(t -> t.getType().equals("BUG")).count() * 100.0) / 100.0;
        scorReq = Math.round(scorReq / (double) ticks.stream().filter(t -> t.getType()
                .equals("FEATURE_REQUEST")).count() * 100.0) / 100.0;
        scorUIFeed = Math.round(scorUIFeed / (double) ticks.stream().filter(t -> t.getType()
                .equals("UI_FEEDBACK")).count() * 100.0) / 100.0;

        // doar la afisare intreb ce comanda trebuie sa mai fac
        ObjectNode typeNode = mapper.createObjectNode();
        if (command.equals("generateCustomerImpactReport")) {
            typeNode.put("BUG", scorBug);
            typeNode.put("FEATURE_REQUEST", scorReq);
            typeNode.put("UI_FEEDBACK", scorUIFeed);
            reportNode.set("customerImpactByType", typeNode);
        }
        if (command.equals("generateTicketRiskReport")) {
            // fac altceva cu acele rezultate
            typeNode.put("BUG", getRiskInterv(scorBug));
            typeNode.put("FEATURE_REQUEST", getRiskInterv(scorReq));
            typeNode.put("UI_FEEDBACK", getRiskInterv(scorUIFeed));
            reportNode.set("riskByType", typeNode);
        }

        return reportNode;
    }
}
