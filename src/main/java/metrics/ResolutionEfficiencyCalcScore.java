package metrics;

import tickets.BugTicket;
import tickets.FeatureRequestTicket;
import tickets.Ticket;
import tickets.UIFeedbackTicket;

import static metrics.MetricsConst.normalizeScore;

public class ResolutionEfficiencyCalcScore {

    public static double calculateEfficiency(Ticket tick) {
        double scoreRisk = 0;

        if (tick.getType().equals("BUG")) {
            BugTicket bug = (BugTicket) tick;
            // (bugFrequency + severityFactor) × 10 / daysToResolve
            scoreRisk = (MetricsConst.freqToNumber(bug.getFrequency())
                    + MetricsConst.severityToNumber(bug.getSeverity())) * 10
                    / tick.getDaysToResolve();
            return normalizeScore(scoreRisk, MetricsConst.BUG_MAX_EFF);
        } else if (tick.getType().equals("FEATURE_REQUEST")) {
            FeatureRequestTicket req = (FeatureRequestTicket) tick;
            scoreRisk = (MetricsConst.businValToNumber(req.getBusinessValue())
                    + MetricsConst.costumerDemToNumber(req.getCustomerDemand()))
                    / tick.getDaysToResolve();
            return normalizeScore(scoreRisk, MetricsConst.FEATURE_REQ_MAX_EFF);
        } else {
            UIFeedbackTicket uif = (UIFeedbackTicket) tick;
            scoreRisk = (uif.getUsabilityScore() + MetricsConst.businValToNumber
                    (uif.getBusinessValue())) / tick.getDaysToResolve();
            return normalizeScore(scoreRisk, MetricsConst.UI_FEED_MAX_EFF);
        }
    }
}
