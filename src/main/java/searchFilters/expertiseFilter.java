package searchFilters;

import users.Developer;
import java.util.List;
import java.util.stream.Collectors;

public class expertiseFilter implements FilterDev {

    private final String expertise;

    public expertiseFilter(String expertise) {
        this.expertise = expertise;
    }

    @Override
    public List<Developer> apply(List<Developer> developers) {
        return developers.stream().filter(d -> d.getExpertiseArea().equals(expertise))
        .collect(Collectors.toList());
    }
}
