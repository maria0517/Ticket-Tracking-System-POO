package searchfilters;

import users.Developer;
import java.util.List;

public final class SearchStrtaegyDev {

    // identic si la fel ca la tickete
    private final List<FilterDev> filters;

    public SearchStrtaegyDev(final List<FilterDev> filters) {
        this.filters = filters;
    }

    /**
     * primesc toti developerii si ii intorc doar pe cei care "trec" de toate filtrele
     */
    public List<Developer> search(final List<Developer> developers) {
        List<Developer> result = developers;

        for (FilterDev filter : filters) {
            // aplicam filtrul pe acelasi set!!!!!
            result = filter.apply(result);
        }

        return result;
    }
}
