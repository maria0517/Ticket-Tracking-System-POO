package searchFilters;

import users.Developer;
import java.util.List;


public interface FilterDev {
    List<Developer> apply(List<Developer> assigDev);

}
