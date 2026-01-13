package metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tickets.Ticket;

import java.util.List;

import static metrics.CustomerImpCalcScore.calculateImpact;
import static metrics.MetricsConst.getRiskInterv;
import static metrics.StabilityCheck.stabilityTest;
import static metrics.TicketRiskCalcScore.calculateRisk;

public class ReportOutputGenerator {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectNode reportFormat (List<Ticket> ticks, String command) {
        ObjectNode reportNode = mapper.createObjectNode();
        // acum trebuie sa generez raportul (il fac strict pentru afisare


        // cate sunt din fiecare
        ObjectNode tickestByTypeNode = mapper.createObjectNode();
        tickestByTypeNode.put("BUG", ticks.stream().
                filter(t -> t.getType().equals("BUG")).count());
        tickestByTypeNode.put("FEATURE_REQUEST", ticks.stream().
                filter(t -> t.getType().equals("FEATURE_REQUEST")).count());
        tickestByTypeNode.put("UI_FEEDBACK", ticks.stream().
                filter(t -> t.getType().equals("UI_FEEDBACK")).count());
        if (command.equals("appStabilityReport")) {
            reportNode.put("totalOpenTickets", ticks.size());
            reportNode.set("openTicketsByType", tickestByTypeNode);
        } else {
            reportNode.put("totalTickets", ticks.size());
            reportNode.set("ticketsByType", tickestByTypeNode);
        }


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
        if (command.equals("appStabilityReport")) {
            reportNode.set("openTicketsByPriority", tickestByPriorNode);
        } else {
            reportNode.set("ticketsByPriority", tickestByPriorNode);
        }

        // dupa vin calculele puternice

        double scorBugImp = 0;
        double scorReqImp = 0;
        double scorUIFeedImp = 0;
        double scorBugRisk = 0;
        double scorReqRisk = 0;
        double scorUIFeedRisk = 0;
        for (Ticket tick : ticks) {
            if (command.equals("generateCustomerImpactReport")
                || command.equals("appStabilityReport")) {
                if (tick.getType().equals("BUG")) {
                    scorBugImp += calculateImpact(tick);
                } else if (tick.getType().equals("FEATURE_REQUEST")) {
                    scorReqImp += calculateImpact(tick);
                } else {
                    scorUIFeedImp += calculateImpact(tick);
                }
            }
            if (command.equals("generateTicketRiskReport")
                || command.equals("appStabilityReport")) {
                if (tick.getType().equals("BUG")) {
                    scorBugRisk += calculateRisk(tick);
                } else if (tick.getType().equals("FEATURE_REQUEST")) {
                    scorReqRisk += calculateRisk(tick);
                } else {
                    scorUIFeedRisk += calculateRisk(tick);
                }
            }
        }

        // am scorurile
        // le impart la nr de tick specifice si afisez
        scorBugImp = Math.round(scorBugImp / (double) ticks.stream().
                filter(t -> t.getType().equals("BUG")).count() * 100.0) / 100.0;
        scorReqImp = Math.round(scorReqImp / (double) ticks.stream().filter(t -> t.getType()
                .equals("FEATURE_REQUEST")).count() * 100.0) / 100.0;
        scorUIFeedImp = Math.round(scorUIFeedImp / (double) ticks.stream().filter(t -> t.getType()
                .equals("UI_FEEDBACK")).count() * 100.0) / 100.0;
        scorBugRisk = Math.round(scorBugRisk / (double) ticks.stream().
                filter(t -> t.getType().equals("BUG")).count() * 100.0) / 100.0;
        scorReqRisk = Math.round(scorReqRisk / (double) ticks.stream().filter(t -> t.getType()
                .equals("FEATURE_REQUEST")).count() * 100.0) / 100.0;
        scorUIFeedRisk = Math.round(scorUIFeedRisk / (double) ticks.stream().filter(t -> t.getType()
                .equals("UI_FEEDBACK")).count() * 100.0) / 100.0;

        // doar la afisare intreb ce comanda trebuie sa mai fac
        ObjectNode typeRiskNode = mapper.createObjectNode();

        if (command.equals("generateTicketRiskReport")
                || command.equals("appStabilityReport")) {
            // fac altceva cu acele rezultate
            typeRiskNode.put("BUG", getRiskInterv(scorBugRisk));
            typeRiskNode.put("FEATURE_REQUEST", getRiskInterv(scorReqRisk));
            typeRiskNode.put("UI_FEEDBACK", getRiskInterv(scorUIFeedRisk));
            reportNode.set("riskByType", typeRiskNode);
        }

        ObjectNode typeImpNode = mapper.createObjectNode();
        if (command.equals("generateCustomerImpactReport")
            || command.equals("appStabilityReport")) {
            typeImpNode.put("BUG", scorBugImp);
            typeImpNode.put("FEATURE_REQUEST", scorReqImp);
            typeImpNode.put("UI_FEEDBACK", scorUIFeedImp);
            if (command.equals("appStabilityReport")) {
                reportNode.set("impactByType", typeImpNode);
            } else {
                reportNode.set("customerImpactByType", typeImpNode);
            }
        }
        
        // aici doar pentru ultima trebuie sa vad stabilitatea
        if (command.equals("appStabilityReport")) {
            reportNode.put("appStability", stabilityTest(scorBugImp, scorReqImp, scorUIFeedImp,
           getRiskInterv(scorBugRisk), getRiskInterv(scorReqRisk), getRiskInterv(scorUIFeedRisk)));
        }
        return reportNode;
    }
}
