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

import static metrics.PerformanceOutput.PerfScoreOutput;

public class PerformanceReport {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ArrayNode generatePerformanceReport(List<Ticket> ticksReport,
            List<User> allUsers, Manager manPrinc) {
        ArrayNode reportNode = mapper.createArrayNode();
        List<Developer> reportDevs = new ArrayList<>();
        for (User u : allUsers) {
            if (u.getRole().equals("DEVELOPER") &&
            manPrinc.getSubordinates().contains(u.getUsername())) {
                // un dev valid
                reportDevs.add((Developer) u);
            }
        }
        // am toti astia, ii sortez lexicografic
        reportDevs.sort(Comparator.comparing(Developer::getUsername));

        // acum pentru fiecare dev, ma pun si ii calc toate alea si
        // returnez ca un arraynode ca sa imi fie mai usor
        for (Developer dev : reportDevs) {
            reportNode.add(PerfScoreOutput(ticksReport, dev));
        }
        return reportNode;
    }
}
