package searchfilters;

import tickets.Ticket;
import java.util.List;
import java.util.ArrayList;

public final class KeywordFilter implements FilterTick {

    private final List<String> keywords;

    public KeywordFilter(final List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public List<Ticket> apply(final List<Ticket> tickets) {
        List<Ticket> result = new ArrayList<>();

        for (Ticket t : tickets) {
            String title = t.getTitle();
            String desc = t.getDescription();

            // daca nu am campurile, le las goale
            if (title == null) {
                title = "";
            }
            if (desc == null) {
                desc = "";
            }
            title = title.toLowerCase();
            desc = desc.toLowerCase();

            for (String k : keywords) {
                if (title.contains(k.toLowerCase()) || desc.contains(k.toLowerCase())) {
                    result.add(t);
                    // am gasit am terminat
                    break;
                }
            }
        }
        return result;
    }
}
