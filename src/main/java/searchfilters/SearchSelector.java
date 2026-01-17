package searchfilters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import milestones.Milestone;
import tickets.Ticket;
import users.Developer;
import users.Manager;
import users.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static main.ViewAfterSearch.viewSearchedDevs;
import static main.ViewAfterSearch.viewSearchedTickets;
import static searchfilters.Filters.parseDEVFiltersFromJson;
import static searchfilters.Filters.parseTICKFiltersFromJson;

public class SearchSelector {

    private SearchSelector() { }

    public static ObjectNode performSearch(final JsonNode cmd, final User util,
          final List<User> allUsers, final Map<Integer, Ticket> allTickets,
          final Map<String, Milestone> allMilestones) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();

        JsonNode filtersNode = cmd.get("filters");
        resultNode.put("command", cmd.get("command").asText());
        resultNode.put("username", cmd.get("username").asText());
        resultNode.put("timestamp", cmd.get("timestamp").asText());
        resultNode.put("searchType", filtersNode.get("searchType").asText());
        if (util.getRole().equals("MANAGER")) {
            // aici trebuie sa vad ce vrea sa caute; dev sau tichete
            Manager man = (Manager) util;
            String searchType = cmd.get("filters").get("searchType").asText();
            if (searchType.equals("DEVELOPER")) {
                // trebuie sa caut dev (toti din subordinea lui)
                List<FilterDev> filters = parseDEVFiltersFromJson(filtersNode);
                // acum trebuie sa iau developerii din echipa amicului
                // ca eu i am pus ca si stringuri cu numele ((
                List<Developer> devsToSearch = new ArrayList<>();
                for (String nume : man.getSubordinates()) {
                    for (User u : allUsers) {
                        if (u.getUsername().equals(nume)) {
                            // am gasit un developer
                            devsToSearch.add((Developer) u);
                        }
                    }
                }
                // acum am tot ce imi trebuie -> fac cautarea
                List<Developer> devsDone = new SearchStrtaegyDev(filters).
                        search(devsToSearch);
                // ipotetic gata -> afisarea dupa
                devsDone.sort(Comparator.comparing(Developer::getUsername));
                resultNode.put("results", viewSearchedDevs(devsDone));
            } else {
                // tichete; toate din sistem
                // mai intai iau filtrele
                List<FilterTick> filters = parseTICKFiltersFromJson(filtersNode,
                        null, allMilestones);
                List<Ticket> tickDone = new SearchStrategyTick(filters).
                        search(new ArrayList<>(allTickets.values()));
                resultNode.put("results", viewSearchedTickets(tickDone,
                        filtersNode, true));
            }
        } else {
            // am un developer si cauta doar tichete
            // open din toate milestoneurile din crae face parte
            Developer dev = (Developer) util;
            List<Ticket> tickToSearch = new ArrayList<>();
            for (String milName : dev.getMilName()) {
                for (Integer id : allMilestones.get(milName).getTickets()) {
                    // din toate cele ale milestoneului, doar cele deschise
                    if (allTickets.get(id).getStatus().equals("OPEN")) {
                        tickToSearch.add(allTickets.get(id));
                    }
                }
            }
            // acum am toate tichetele cred eu
            List<FilterTick> filters = parseTICKFiltersFromJson(filtersNode,
                    (Developer) util, allMilestones);
            if (filters.size() == 0) {
                // nu am niciun filtru, il tratez ca pe viewTickets
                resultNode.put("results",
                        viewSearchedTickets(new ArrayList<>(
                                allTickets.values()), filtersNode, false));
            } else {
                List<Ticket> tickDone = new SearchStrategyTick(filters).
                        search(tickToSearch);
                resultNode.put("results", viewSearchedTickets(
                        tickDone, filtersNode, false));
            }
        }
        return resultNode;
    }
}
