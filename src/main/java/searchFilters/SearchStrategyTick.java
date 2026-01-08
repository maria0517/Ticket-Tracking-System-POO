package searchFilters;

import tickets.Ticket;
import java.util.List;

public class SearchStrategyTick {

    // lista cu filtre pe care trebuie sa le aplic pe lista de tichete
    private List<FilterTick> allFilters;

    public SearchStrategyTick(List<FilterTick> filters) {
        this.allFilters = filters;
    }

    public List<Ticket> search(List<Ticket> tickets) {
        List<Ticket> result = null;
        for (FilterTick filter : allFilters) {
            result = filter.apply(tickets);
            tickets = result;
        }
        return result;
    }
}
