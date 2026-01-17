package metrics;

import tickets.BugTicket;
import tickets.FeatureRequestTicket;
import tickets.Ticket;
import tickets.UIFeedbackTicket;

import static metrics.MetricsConst.normalizeScore;


public final class TicketRiskCalcScore {
    // doar aici imi trebuie 11 asta
    // il definesc ca si const aici
    public static final int INT_11 = 11;

    private TicketRiskCalcScore() {
    }

    /**
     * calculeaza efectiv riskul pentru a l pune in raport
     */
    public static double calculateRisk(final Ticket tick) {
        double scoreRisk = 0;

        if (tick.getType().equals("BUG")) {
            BugTicket bug = (BugTicket) tick;
            //frequency × severityFactor
            scoreRisk =  MetricsConst.freqToNumber(bug.getFrequency())
                 *  MetricsConst.severityToNumber(bug.getSeverity());
            return normalizeScore(scoreRisk, MetricsConst.BUG_MAX_RISK);
        } else if (tick.getType().equals("FEATURE_REQUEST")) {
            FeatureRequestTicket req = (FeatureRequestTicket) tick;
            // businessValue + customerDemand
            scoreRisk = MetricsConst.businValToNumber(req.getBusinessValue())
                    + MetricsConst.costumerDemToNumber(req.getCustomerDemand());
            return normalizeScore(scoreRisk, MetricsConst.FEATURE_REQ_MAX_RISK);
        } else {
            UIFeedbackTicket uif = (UIFeedbackTicket) tick;
           // Formula: (11 − usabilityScore) × businessValue
            scoreRisk = (INT_11 - uif.getUsabilityScore())
                    * MetricsConst.businValToNumber(uif.getBusinessValue());
            return normalizeScore(scoreRisk, MetricsConst.UI_FEED_MAX_RISK);
        }
    }
}
