package metrics;

import tickets.BugTicket;
import tickets.FeatureRequestTicket;
import tickets.Ticket;
import tickets.UIFeedbackTicket;

import static metrics.MetricsConst.normalizeScore;


public class TicketRiskCalcScore {

    public static double calculateRisk(Ticket tick) {
        double scoreRisk = 0;

        if (tick.getType().equals("BUG")) {
            BugTicket bug = (BugTicket) tick;
            //frequency × severityFactor
            scoreRisk =  MetricsConst.freqToNumber(bug.getFrequency())
                 *  MetricsConst.severityToNumber(bug.getSeverity());
            return normalizeScore(scoreRisk, MetricsConst.BUG_MAX_RISK);
        } else if (tick.getType().equals("FEATURE_REQUEST")) {
            FeatureRequestTicket req = (FeatureRequestTicket) tick;
            //Formula: businessValue + customerDemand
            scoreRisk = MetricsConst.businValToNumber(req.getBusinessValue())
                    + MetricsConst.costumerDemToNumber(req.getCustomerDemand());
            return normalizeScore(scoreRisk, MetricsConst.FEATURE_REQ_MAX_RISK);
        } else {
            UIFeedbackTicket uif = (UIFeedbackTicket) tick;
           // Formula: (11 − usabilityScore) × businessValue
            scoreRisk = (11 - uif.getUsabilityScore())
                    * MetricsConst.businValToNumber(uif.getBusinessValue());
            return normalizeScore(scoreRisk, MetricsConst.UI_FEED_MAX_RISK);
        }
    }
}
