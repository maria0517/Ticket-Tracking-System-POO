package searchfilters;

import users.Developer;
import java.util.List;
import java.util.stream.Collectors;

public final class SeniorityFilter implements FilterDev {

    private final String seniority;

    public SeniorityFilter(final String seniority) {
        this.seniority = seniority;
    }

    @Override
    public List<Developer> apply(final List<Developer> developers) {
        return developers.stream().filter(d -> d.getSeniority().equals(seniority))
           .collect(Collectors.toList());
    }
}

