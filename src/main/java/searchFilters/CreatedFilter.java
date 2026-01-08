package searchFilters;

import tickets.Ticket;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class CreatedFilter implements FilterTick {

    public static final int AT = 0;
    public static final int BEFORE = -1;
    public static final int AFTER = 1;

    private final LocalDate date;
    private final int type;

    public CreatedFilter(String date, int type) {
        this.date = LocalDate.parse(date);
        this.type = type;
    }

    @Override
    public List<Ticket> apply(List<Ticket> tickets) {
        List<Ticket> result = new ArrayList<>();
        for (Ticket t : tickets) {
            LocalDate created = LocalDate.parse(t.getCreatedAt());
            if (type == AT && created.isEqual(date)) {
                result.add(t);
            }

            if (type == BEFORE && created.isBefore(date)) {
                result.add(t);
            }

            if (type == AFTER && created.isAfter(date)) {
                result.add(t);
            }
        }
        // returnez ce am obtinut
        return result;
    }
}

