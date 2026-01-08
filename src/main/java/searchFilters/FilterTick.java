package searchFilters;

import tickets.Ticket;
import java.util.List;

public interface FilterTick {
    // metoda comuna pentru toate filtrele
    /**
     * @param tickets
     * @return filtered_tickets
     */
    List<Ticket> apply(List<Ticket> tickets);
}