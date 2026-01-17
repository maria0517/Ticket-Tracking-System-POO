package metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tickets.Ticket;
import users.Developer;
import constants.Const;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;


public final class PerformanceOutput {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private PerformanceOutput() {
        // prevenire instantiere
    }

    /**
     * face outputul si imi si calculeaza ce trebuie sa afisez
     */
    public static ObjectNode perfScoreOutput(final List<Ticket> ticksForRep, final Developer dev) {
        ObjectNode devNode = MAPPER.createObjectNode();
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
        devNode.put("averageResolutionTime", Math.round(averageResolutionTime
                * MetricsConst.ONE_HUNDRED) / MetricsConst.ONE_HUNDRED);
        // mai e asta ultima
        if (nrTicksClosed == 0) {
            devNode.put("performanceScore", 0.0);
        } else {
            devNode.put("performanceScore", Math.round(calcPerfScore(dev, ticksForRep,
                    averageResolutionTime, nrTicksClosed) * MetricsConst.ONE_HUNDRED)
                    / MetricsConst.ONE_HUNDRED);
            // il si pastrez pentru ca imi trebuie pentru filtrare
            dev.setPerforScore(Math.round(calcPerfScore(dev, ticksForRep,
                    averageResolutionTime, nrTicksClosed) * MetricsConst.ONE_HUNDRED)
                    / MetricsConst.ONE_HUNDRED);
        }
        devNode.put("seniority", dev.getSeniority());

        return devNode;
    }

    /**
     * calculeaza efectiv performance scorul
     */
    public static double calcPerfScore(final Developer dev, final List<Ticket> ticksForRep,
              final double averageResolutionTime, final int nrTicksCLosed) {
        int nrBugTick = (int) dev.getAssignedTckets().stream().filter(t ->
                t.getType().equals("BUG")).count();
        int nrReqTick = (int) dev.getAssignedTckets().stream().filter(t ->
                t.getType().equals("FEATURE_REQUEST")).count();
        int nrUITick = (int) dev.getAssignedTckets().stream().filter(t ->
                t.getType().equals("UI_FEEDBACK")).count();
        if (dev.getSeniority().equals("JUNIOR")) {
            //Math.max(0, 0.5 * closedTickets - ticketDiversityFactor) + seniorityBonus
            return Math.max(0, Const.JUMATE_1 * nrTicksCLosed - ticketDiversityFactor(nrBugTick,
                    nrReqTick, nrUITick)) + MetricsConst.getSeniorityBonus("JUNIOR");
        } else if (dev.getSeniority().equals("MID")) {
            return Math.max(0, Const.JUMATE_1 * nrTicksCLosed + Const.ZERO_SAPTE * highPriorityTickets(dev)
                    - Const.O_TREIME * averageResolutionTime) + MetricsConst.getSeniorityBonus("MID");

        } else {
            // senior
            return Math.max(0, Const.JUMATE_1 * nrTicksCLosed + 1.0 * highPriorityTickets(dev)
                    - Const.JUMATE_1 * averageResolutionTime) + MetricsConst.getSeniorityBonus("SENIOR");

        }

    }

    /**
     * nr de tichete cu prior maxima
     */
    public static double highPriorityTickets(final Developer dev) {
        double nrTicks = 0;
        for (Ticket t : dev.getAssignedTckets()) {
            if (t.getBusinessPriority().equals("HIGH")
                    || t.getBusinessPriority().equals("CRITICAL")) {
                nrTicks++;
            }
        }
        return nrTicks;
    }

    /**
     * calc avg la tichete
     */
    public static double averageResolvedTicketType(final int bug,
                 final int feature, final int ui) {
        return (bug + feature + ui) / Const.UN_TREI_DOUBLE;
    }

    /**
     * calc deviatia maxima
     */
    public static double standardDeviation(final int bug, final int feature, final int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);
        double variance = (Math.pow(bug - mean, 2)
                + Math.pow(feature - mean, 2) + Math.pow(ui - mean, 2)) / Const.UN_TREI_DOUBLE;
        return Math.sqrt(variance);
    }

    /**
     * calc diversitate la tichete
     */
    public static double ticketDiversityFactor(final int bug, final int feature, final int ui) {
        double mean = averageResolvedTicketType(bug, feature, ui);

        // daca nu exista tichete, diversitatea este 0
        if (mean == 0.0) {
            return 0.0;
        }

        double std = standardDeviation(bug, feature, ui);
        return std / mean;
    }

}
