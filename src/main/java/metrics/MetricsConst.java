package metrics;

public class MetricsConst {
    // aici o sa am toate metricile
    // se pot adauga cate se mai doresc pe langa cele impuse
    public final static int BUG_MAX_CUSTOMER = 48;
    public final static int FEATURE_REQ_MAX_CUSTOMER = 100;
    public final static int UI_FEED_MAX_CUSTOMER = 100;

    public final static int BUG_MAX_RISK = 12;
    public final static int FEATURE_REQ_MAX_RISK = 20;
    public final static int UI_FEED_MAX_RISK = 100;


    public static int freqToNumber (String frequency) {
        return switch (frequency) {
            case "RARE" -> 1;
            case "OCCASIONAL" -> 2;
            case "FREQUENT" -> 3;
            case "ALWAYS" -> 4;
            default -> 0;
        };
    }

    public static int bussPriorToNumber (String businessPriority) {
        return switch (businessPriority) {
            case "LOW" -> 1;
            case "MEDIUM" -> 2;
            case "HIGH" -> 3;
            case "CRITICAL" -> 4;
            default -> 0;
        };
    }

    public static int severityToNumber (String severity) {
        return switch (severity) {
            case "MINOR" -> 1;
            case "MODERATE" -> 2;
            case "SEVERE" -> 3;
            default -> 0;
        };
    }

    public static int businValToNumber (String businessValue) {
        return switch (businessValue) {
            case "S" -> 1;
            case "M" -> 3;
            case "L" -> 6;
            case "XL" -> 10;
            default -> 0;
        };
    }

    public static int costumerDemToNumber (String costumerDemand) {
        return switch (costumerDemand) {
            case "LOW" -> 1;
            case "MEDIUM" -> 3;
            case "HIGH" -> 6;
            case "VERY_HIGH" -> 10;
            default -> 0;
        };
    }

    public static String getRiskInterv (double score) {
        if (score <= 24) {
            return "NEGLIGIBLE";
        } else if (score <= 49) {
            return "MODERATE";
        } else if (score <= 74) {
            return "SIGNIFICANT";
        } else {
            return "MAJOR";
        }
    }

    // functia de normalizare sa o iau mai usor
    public static double normalizeScore(double base, double max) {
        return Math.min(100.0, (base * 100.0) / max);
    }
}
