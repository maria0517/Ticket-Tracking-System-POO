package searchFilters;

import users.Developer;
import java.util.List;

public class SearchStrtaegyDev {

    // identic si la fel ca la tickete
    private final List<FilterDev> filters;

    public SearchStrtaegyDev(List<FilterDev> filters) {
        this.filters = filters;
    }

    public List<Developer> search(List<Developer> developers) {
        List<Developer> result = developers;

        for (FilterDev filter : filters) {
            // aplicai filtrul pe acelasi set!!!!!
            result = filter.apply(developers);
            developers = result;
        }

        return result;
    }
}
