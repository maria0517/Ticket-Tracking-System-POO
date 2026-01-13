package searchfilters;

import users.Developer;
import java.util.List;


public interface FilterDev {
    /**
     * aplica un filtru pe o lista de developeri
     */
    List<Developer> apply(List<Developer> assigDev);
}
