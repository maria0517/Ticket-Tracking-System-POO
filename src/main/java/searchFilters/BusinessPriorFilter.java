package searchFilters;

import tickets.Ticket;
import java.util.List;

public class BusinessPriorFilter implements FilterTick {

    private String requiredPriority;

    public BusinessPriorFilter(String requiredPriority) {
        this.requiredPriority = requiredPriority;
    }

    // efectiv aplic filtrare dupa business prior
    @Override
    public List<Ticket> apply(List<Ticket> tickets) {
        return tickets.stream().filter(t -> requiredPriority.
        equals(t.getBusinessPriority())).toList();
    }
}
