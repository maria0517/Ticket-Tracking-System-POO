package metrics;

import constants.Const;

public final class MetricsConst {
    private MetricsConst() { }

    public static final double ONE_HUNDRED = 100.0;
    // aici o sa am toate metricile
    // se pot adauga cate se mai doresc pe langa cele impuse
    public static final int BUG_MAX_CUSTOMER = 48;
    public static final int FEATURE_REQ_MAX_CUSTOMER = 100;
    public static final int UI_FEED_MAX_CUSTOMER = 100;

    public static final int BUG_MAX_RISK = 12;
    public static final int FEATURE_REQ_MAX_RISK = 20;
    public static final int UI_FEED_MAX_RISK = 100;

    public static final int BUG_MAX_EFF = 70;
    public static final int FEATURE_REQ_MAX_EFF = 20;
    public static final int UI_FEED_MAX_EFF = 20;


    /**
     * convertor frecventa de la string la val
     */
    public static int freqToNumber(final String frequency) {
        return switch (frequency) {
            case "RARE" -> 1;
            case "OCCASIONAL" -> 2;
            case "FREQUENT" -> Const.UN_TREI_INT;
            case "ALWAYS" -> Const.UN_PATRU_INT;
            default -> 0;
        };
    }

    /**
     * convertor prior de la string la val
     */
    public static int bussPriorToNumber(final String businessPriority) {
        return switch (businessPriority) {
            case "LOW" -> 1;
            case "MEDIUM" -> 2;
            case "HIGH" -> Const.UN_TREI_INT;
            case "CRITICAL" -> Const.UN_PATRU_INT;
            default -> 0;
        };
    }

    /**
     * convertor sev de la string la val
     */
    public static int severityToNumber(final String severity) {
        return switch (severity) {
            case "MINOR" -> 1;
            case "MODERATE" -> 2;
            case "SEVERE" -> Const.UN_TREI_INT;
            default -> 0;
        };
    }

    /**
     * convertor val de la string la val
     */
    public static int businValToNumber(final String businessValue) {
        return switch (businessValue) {
            case "S" -> 1;
            case "M" -> Const.UN_TREI_INT;
            case "L" -> Const.UN_SASE_INT;
            case "XL" -> Const.UN_ZECE_INT;
            default -> 0;
        };
    }

    /**
     * convertor cerere de la string la val
     */
    public static int costumerDemToNumber(final String costumerDemand) {
        return switch (costumerDemand) {
            case "LOW" -> 1;
            case "MEDIUM" -> Const.UN_TREI_INT;
            case "HIGH" -> Const.UN_SASE_INT;
            case "VERY_HIGH" -> Const.UN_ZECE_INT;
            default -> 0;
        };
    }

    /**
     * convertor risc de la string la val
     */
    public static String getRiskInterv(final double score) {
        if (score <= Const.UN_DOI_PATRU_INT) {
            return "NEGLIGIBLE";
        } else if (score <= Const.UN_PATRU_NOUA_INT) {
            return "MODERATE";
        } else if (score <= Const.UN_SAPTE_PATRU_INT) {
            return "SIGNIFICANT";
        } else {
            return "MAJOR";
        }
    }

    /**
     *
     * @param senior
     * @return
     */
    public static double getSeniorityBonus(final String senior) {
        return switch (senior) {
            case "JUNIOR" -> Const.UN_CINCI_INT;
            case "MID" -> Const.UN_CINCIZECE_INT;
            case "SENIOR" -> Const.UN_TREIZECI_INT;
            default -> 0;
        };
    }

    /**
     * functia de normalizare sa o iau mai usor
     */
    public static double normalizeScore(final double base, final double max) {
        return Math.min(ONE_HUNDRED, (base * ONE_HUNDRED) / max);
    }
}
