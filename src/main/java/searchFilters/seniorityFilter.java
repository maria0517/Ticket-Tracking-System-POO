package searchFilters;

import users.Developer;
import java.util.List;
import java.util.stream.Collectors;

public class seniorityFilter implements FilterDev {

    private final String seniority;

    public seniorityFilter(String seniority) {
        this.seniority = seniority;
    }

    @Override
    public List<Developer> apply(List<Developer> developers) {
        return developers.stream().filter(d -> d.getSeniority().equals(seniority))
           .collect(Collectors.toList());
    }
}

