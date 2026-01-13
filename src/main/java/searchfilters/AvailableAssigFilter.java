package searchfilters;

import main.Milestone;
import tickets.Ticket;
import users.Developer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class AvailableAssigFilter implements FilterTick {
    private final boolean isAvailable;

    // pt a putea face validarea
    private Developer dev;
    private Map<String, Milestone> allMilestones;

    public AvailableAssigFilter(final boolean isAvailable, final Developer dev,
             final Map<String, Milestone> allMilestones) {
        this.isAvailable = isAvailable;
        this.dev = dev;
        this.allMilestones = allMilestones;
    }

    @Override
    public List<Ticket> apply(final List<Ticket> tickets) {
        // trebuie sa vad daca ticketele filtrate pot fi asignate developerului care face apelul
        List<Ticket> result = new ArrayList<>();
        for (Ticket tick : tickets) {
            if (dev.validAssigment(tick, allMilestones).equals("valid")) {
                // e bun il adaug
                result.add(tick);
            }
        }
        return result;
    }
}
