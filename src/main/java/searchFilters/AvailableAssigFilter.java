package searchFilters;

import main.Milestone;
import tickets.Ticket;
import users.Developer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AvailableAssigFilter implements FilterTick { // <- implementăm interfața
    private final boolean isAvailable;

    // pt a putea face validarea
    private Developer dev;
    private Map<String, Milestone> allMilestones;

    public AvailableAssigFilter(boolean isAvailable,
              Developer dev, Map<String, Milestone> allMilestones) {
        this.isAvailable = isAvailable;
        this.dev = dev;
        this.allMilestones = allMilestones;
    }

    @Override
    public List<Ticket> apply(List<Ticket> tickets) {
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
