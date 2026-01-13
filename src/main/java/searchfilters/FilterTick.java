package searchfilters;

import tickets.Ticket;
import java.util.List;

public interface FilterTick {
    // metoda comuna pentru toate filtrele
    /**
     * aplica un filtru pe o lista de developeri
     */
    List<Ticket> apply(List<Ticket> tickets);
}
