package metrics;

import tickets.BugTicket;
import tickets.FeatureRequestTicket;
import tickets.Ticket;
import tickets.UIFeedbackTicket;

import static metrics.MetricsConst.normalizeScore;

public final class CustomerImpCalcScore {

    private CustomerImpCalcScore() { }

    /**
     * return la scorul de impact
     */
    public static double calculateImpact(final Ticket tick) {
        double baseScore = 0;

        if (tick.getType().equals("BUG")) {
            BugTicket bug = (BugTicket) tick;
            baseScore = MetricsConst.freqToNumber(bug.getFrequency())
                    * MetricsConst.bussPriorToNumber(bug.getBusinessPriority())
                    * MetricsConst.severityToNumber(bug.getSeverity());
            return normalizeScore(baseScore, MetricsConst.BUG_MAX_CUSTOMER);
        } else if (tick.getType().equals("FEATURE_REQUEST")) {
            FeatureRequestTicket req = (FeatureRequestTicket) tick;
            baseScore = MetricsConst.businValToNumber(req.getBusinessValue())
                    * MetricsConst.costumerDemToNumber(req.getCustomerDemand());
            return normalizeScore(baseScore, MetricsConst.FEATURE_REQ_MAX_CUSTOMER);
        } else {
            // UI_FEEDBACK
            UIFeedbackTicket uif = (UIFeedbackTicket) tick;
            baseScore = MetricsConst.businValToNumber(uif.getBusinessValue())
                         * uif.getUsabilityScore();
            return normalizeScore(baseScore, MetricsConst.UI_FEED_MAX_CUSTOMER);
        }
    }
}
