package searchfilters;

import tickets.Ticket;
import java.util.List;

public final class BusinessPriorFilter implements FilterTick {

    private String requiredPriority;

    public BusinessPriorFilter(final String requiredPriority) {
        this.requiredPriority = requiredPriority;
    }

    // efectiv aplic filtrare dupa business prior
    @Override
    public List<Ticket> apply(final List<Ticket> tickets) {
        return tickets.stream().filter(t -> requiredPriority.
        equals(t.getBusinessPriority())).toList();
    }
}
