package searchfilters;

import users.Developer;
import java.util.List;
import java.util.stream.Collectors;

public final class ExpertiseFilter implements FilterDev {

    private final String expertise;

    public ExpertiseFilter(final String expertise) {
        this.expertise = expertise;
    }

    @Override
    public List<Developer> apply(final List<Developer> developers) {
        return developers.stream().filter(d -> d.getExpertiseArea().equals(expertise))
        .collect(Collectors.toList());
    }
}
