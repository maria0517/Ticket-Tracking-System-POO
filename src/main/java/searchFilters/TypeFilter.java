package searchFilters;

import tickets.Ticket;
import java.util.List;
import java.util.stream.Collectors;

public class TypeFilter implements FilterTick {

    private final String type;

    public TypeFilter(String type) {
        this.type = type;
    }

    @Override
    public List<Ticket> apply(List<Ticket> tickets) {
        return tickets.stream().filter(t -> t.getType().equals(type))
                .collect(Collectors.toList());
    }
}
