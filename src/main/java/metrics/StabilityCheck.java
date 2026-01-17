package metrics;

public final class StabilityCheck {

    private StabilityCheck() { }

    public static final double MAX_SCORE_STABLE = 50.0;

    /**
     * @return daca app este stabila sau nu
     */
    public static String stabilityTest(final double scorBugImp, final double scorReqImp,
          final double scorUIFeedImp, final String bugRisk, final String reqRisk,
           final String uiFeedRisk) {
        // return "hello";
        if (scorBugImp <= MAX_SCORE_STABLE && scorReqImp <= MAX_SCORE_STABLE
                && scorUIFeedImp <= MAX_SCORE_STABLE && bugRisk.equals("NEGLIGIBLE")
                && reqRisk.equals("NEGLIGIBLE") && uiFeedRisk.equals("NEGLIGIBLE")) {
                return "STABLE";
        } else if (bugRisk.equals("SIGNIFICANT") || reqRisk.equals("SIGNIFICANT")
                    || uiFeedRisk.equals("SIGNIFICANT")) {
                return "UNSTABLE";
        }
        // sunt in alte cazuri
        // asta e by_default
        return "PARTIALLY_STABLE";
    }
}
