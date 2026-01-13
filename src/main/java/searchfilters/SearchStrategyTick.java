package searchfilters;

import tickets.Ticket;
import java.util.List;

public final class SearchStrategyTick {

    // lista cu filtre pe care trebuie sa le aplic pe lista de tichete
    private List<FilterTick> allFilters;

    public SearchStrategyTick(final List<FilterTick> filters) {
        this.allFilters = filters;
    }

    /**
     * primesc tichetele si le triez in functie de filtrele primite
     */
    public List<Ticket> search(final List<Ticket> tickets) {
        List<Ticket> result = tickets;
        for (FilterTick filter : allFilters) {
            result = filter.apply(result);
            // tickets = result;
        }
        return result;
    }
}
