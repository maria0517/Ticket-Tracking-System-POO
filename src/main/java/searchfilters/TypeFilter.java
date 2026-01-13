package searchfilters;

import tickets.Ticket;
import java.util.List;
import java.util.stream.Collectors;

public final class TypeFilter implements FilterTick {

    private final String type;

    public TypeFilter(final String type) {
        this.type = type;
    }

    @Override
    public List<Ticket> apply(final List<Ticket> tickets) {
        // le returnez pe cele care sunt de tipul cerut
        return tickets.stream().filter(t -> t.getType().equals(type))
                .collect(Collectors.toList());
    }
}
