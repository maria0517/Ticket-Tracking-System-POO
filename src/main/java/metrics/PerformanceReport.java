package metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import tickets.Ticket;
import users.Developer;
import users.Manager;
import users.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static metrics.PerformanceOutput.perfScoreOutput;

public final class PerformanceReport {

    private PerformanceReport() { }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * imi intoarce raportul de performanta ca sa l pun direct in output
     */
    public static ArrayNode generatePerformanceReport(final List<Ticket> ticksReport,
           final List<User> allUsers, final Manager manPrinc) {
        ArrayNode reportNode = MAPPER.createArrayNode();
        List<Developer> reportDevs = new ArrayList<>();
        for (User u : allUsers) {
            if (u.getRole().equals("DEVELOPER")
                    && manPrinc.getSubordinates().contains(u.getUsername())) {
                // un dev valid
                reportDevs.add((Developer) u);
            }
        }
        // am toti astia, ii sortez in ordine alfabetica
        reportDevs.sort(Comparator.comparing(Developer::getUsername));

        // acum pentru fiecare dev, ma pun si ii calc toate alea si
        // returnez ca un arraynode ca sa imi fie mai usor
        for (Developer dev : reportDevs) {
            reportNode.add(perfScoreOutput(ticksReport, dev));
        }
        return reportNode;
    }
}
