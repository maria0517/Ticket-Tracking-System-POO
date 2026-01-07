package tickets;

public class UIFeedbackTicket extends Ticket {

    private String uiElementId;
    private String businessValue;
    private int usabilityScore;
    private String screenshotUrl;
    private String suggestedFix;

    private UIFeedbackTicket(Builder b) {
        super(b.id, "UI_FEEDBACK", b.title, b.businessPriority,
                "OPEN", b.expertiseArea, b.description, b.reportedBy, b.createdAt);

        this.uiElementId = b.uiElementId;
        this.businessValue = b.businessValue;
        this.usabilityScore = b.usabilityScore;
        this.screenshotUrl = b.screenshotUrl;
        this.suggestedFix = b.suggestedFix;
    }

    // === BUILDER ===
    public static class Builder {
        private int id;
        private String title;
        private String businessPriority;
        private String expertiseArea;
        private String description;
        private String reportedBy;

        // specifice
        private String uiElementId;
        private String businessValue;
        private int usabilityScore;
        private String screenshotUrl;
        private String suggestedFix;

        private String createdAt;

        // comune
        public Builder setId(int id) { this.id = id; return this; }
        public Builder setTitle(String title) { this.title = title; return this; }
        public Builder setBusinessPriority(String bp) { this.businessPriority = bp; return this; }
        public Builder setExpertiseArea(String area) { this.expertiseArea = area; return this; }
        public Builder setDescription(String desc) { this.description = desc; return this; }
        public Builder setReportedBy(String rb) { this.reportedBy = rb; return this; }

        // specifice
        public Builder setUiElementId(String id) { this.uiElementId = id; return this; }
        public Builder setBusinessValue(String bv) { this.businessValue = bv; return this; }
        public Builder setUsabilityScore(int score) { this.usabilityScore = score; return this; }
        public Builder setScreenshotUrl(String url) { this.screenshotUrl = url; return this; }
        public Builder setSuggestedFix(String fix) { this.suggestedFix = fix; return this; }

        public Builder setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UIFeedbackTicket build() {
            return new UIFeedbackTicket(this);
        }
    }

    // Getteri
    public String getUiElementId() { return uiElementId; }
    public String getBusinessValue() { return businessValue; }
    public int getUsabilityScore() { return usabilityScore; }
    public String getScreenshotUrl() { return screenshotUrl; }
    public String getSuggestedFix() { return suggestedFix; }
}
