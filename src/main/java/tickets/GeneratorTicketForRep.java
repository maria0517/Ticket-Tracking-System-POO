package tickets;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GeneratorTicketForRep {

    private GeneratorTicketForRep() { }

    public static List<Ticket> selectTickets(final String command,
       final Map<Integer, Ticket> allTickets, String timestamp) {

        List<Ticket> resultTickets = new ArrayList<>();

        for (Ticket t : allTickets.values()) {
            if (command.equals("generateCustomerImpactReport")
                    || command.equals("generateTicketRiskReport")
                    || command.equals("appStabilityReport")) {
                if (t.getStatus().equals("OPEN") || t.getStatus().equals("IN_PROGRESS")) {
                    resultTickets.add(t);
                }
            }
            if (command.equals("generateResolutionEfficiencyReport")) {
                // aici trebuie luate doar cele closed sau resolved
                if (t.getStatus().equals("CLOSED") || t.getStatus().equals("RESOLVED")) {
                    resultTickets.add(t);
                }
            }
            if (command.equals("generatePerformanceReport")) {
                if (t.getStatus().equals("CLOSED")) {
                    // calculez luna anterioara
                    LocalDate commandDate = LocalDate.parse(timestamp);
                    YearMonth previousMonth = YearMonth.from(commandDate).minusMonths(1);
                    // calculez luna la care s a finalizat tichetul
                    YearMonth solvedMonth = YearMonth.from(LocalDate.parse(t.getSolvedAt()));
                    if (solvedMonth.equals(previousMonth)) {
                        // ticket valid
                        resultTickets.add(t);
                    }
                }
            }
        }
        // return la lista coresp
        return resultTickets;
    }
}
