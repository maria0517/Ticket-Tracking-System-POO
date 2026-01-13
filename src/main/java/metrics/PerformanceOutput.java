package metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tickets.Ticket;
import users.Developer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PerformanceOutput {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectNode PerfScoreOutput(List<Ticket> ticksForRep, Developer dev) {
        ObjectNode devNode = mapper.createObjectNode();
        devNode.put("username", dev.getUsername());
        // nr de tichete inchise de amic
        int nrTicksClosed = (int) ticksForRep.stream().filter(t ->
             t.getAssignedTo().equals(dev.getUsername())).count();
        devNode.put("closedTickets", nrTicksClosed);
        // acum asta averageResolutionTime
        double averageResolutionTime = 0.0;
        for (Ticket t : dev.getAssignedTckets()) {
            // aici ma pun si ma uit
            if (t.getStatus().equals("CLOSED") && ticksForRep.contains(t)) {
                // am un ticket care e din luna trecuta rezolvat
                // calculez aia
                LocalDate assignedAtDate = LocalDate.parse(t.getAssignedAt());
                LocalDate solvedAtDate = LocalDate.parse(t.getSolvedAt());
                averageResolutionTime += ChronoUnit.DAYS.between(assignedAtDate, solvedAtDate) + 1;

            }
        }
        if (nrTicksClosed == 0) {
            averageResolutionTime = 0;
        } else {
            averageResolutionTime = averageResolutionTime / nrTicksClosed;
        }
        devNode.put("averageResolutionTime", Math.round(averageResolutionTime * 100.0) / 100.0);
        // mai e asta ultima
        if (nrTicksClosed == 0) {
            devNode.put("performanceScore", 0.0);
        } else {
            devNode.put("performanceScore", Math.round(calcPerfScore(dev, ticksForRep,
                    averageResolutionTime, nrTicksClosed) * 100.0) / 100.0);
        }
        devNode.put("seniority", dev.getSeniority());

        return devNode;
    }

    public static double calcPerfScore (Developer dev, List<Ticket> ticksForRep,
                   double averageResolutionTime, int nrTicksCLosed) {
        int nrBugTick = (int) dev.getAssignedTckets().stream().filter(t ->
                t.getType().equals("BUG")).count();
        int nrReqTick = (int) dev.getAssignedTckets().stream().filter(t ->
                t.getType().equals("FEATURE_REQUEST")).count();
        int nrUITick = (int) dev.getAssignedTckets().stream().filter(t ->
                t.getType().equals("UI_FEEDBACK")).count();
        if (dev.getSeniority().equals("JUNIOR")) {
            //Math.max(0, 0.5 * closedTickets - ticketDiversityFactor) + seniorityBonus
            return Math.max(0, 0.5 * nrTicksCLosed - ticketDiversityFactor(nrBugTick,
                    nrReqTick, nrUITick)) + MetricsConst.getSeniorityBonus("JUNIOR");
        } else if (dev.getSeniority().equals("MID")) {
            System.out.println("yfdgfoivu");
            return Math.max(0, 0.5 * nrTicksCLosed + 0.7 * highPriorityTickets(dev)
                    - 0.3 * averageResolutionTime) + MetricsConst.getSeniorityBonus("MID");

        } else {
            // senior
            System.out.println("aoihfbvidsv");
            return Math.max(0, 0.5 * nrTicksCLosed + 1.0 * highPriorityTickets(dev)
                    - 0.5 * averageResolutionTime) + MetricsConst.getSeniorityBonus("SENIOR");

        }

    }

    public static double highPriorityTickets(Developer dev) {
        double nrTicks = 0;
        for (Ticket t : dev.getAssignedTckets()) {
            System.out.println("prioritate ticket" + t.getBusinessPriority());
            if (t.getBusinessPriority().equals("HIGH") || t.getBusinessPriority().equals("CRITICAL")) {
                nrTicks++;
            }
        }
        System.out.println("aici + " + nrTicks);
        return nrTicks;
    }

    // astea sunt date in cerinta
    public static double averageResolvedTicketType(int bug, int feature, int ui) {
        return (bug + feature + ui) / 3.0;
    }

    public static double standardDeviation(int bug, int feature, int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        double variance = (Math.pow(bug - mean, 2) +
                Math.pow(feature - mean, 2) + Math.pow(ui - mean, 2)) / 3.0;
        return Math.sqrt(variance);
    }

    public static double ticketDiversityFactor(int bug, int feature, int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);

        // daca nu exista tichete, diversitatea este 0
        if (mean == 0.0) {
            return 0.0;
        }

        double std = standardDeviation(bug, feature, ui);
        return std / mean;
    }

}
