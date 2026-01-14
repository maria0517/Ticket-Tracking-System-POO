package searchfilters;



import users.Developer;

import java.util.ArrayList;
import java.util.List;

public final class PerformanceFilter implements FilterDev {
    // mai lasam putin

    public static final int BELOW = -1;
    public static final int ABOVE = 1;

    private double score;
    private int type;

    public PerformanceFilter(final String textScore, final int type) {
        this.score = Double.parseDouble(textScore);
        this.type = type;
    }

    @Override
    public List<Developer> apply(final List<Developer> developers) {
        List<Developer> result = new ArrayList<>();
        for (Developer dev : developers) {
            if (type == -1) {
                // sunt pe cazul below
                if (score >= dev.getPerforScore()) {
                    result.add(dev);
                }
            } else if (type == 1) {
                // above
                if (score <= dev.getPerforScore()) {
                    result.add(dev);
                }
            }
        }
    return result;
    }

}
