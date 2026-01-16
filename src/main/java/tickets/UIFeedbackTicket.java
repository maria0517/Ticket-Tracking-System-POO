package tickets;

public final class UIFeedbackTicket extends Ticket {

    private String uiElementId;
    private String businessValue;
    private int usabilityScore;
    private String screenshotUrl;
    private String suggestedFix;

    private UIFeedbackTicket(final Builder b) {
        super(b.id, "UI_FEEDBACK", b.title, b.businessPriority,
                "OPEN", b.expertiseArea, b.description, b.reportedBy, b.createdAt);

        this.uiElementId = b.uiElementId;
        this.businessValue = b.businessValue;
        this.usabilityScore = b.usabilityScore;
        this.screenshotUrl = b.screenshotUrl;
        this.suggestedFix = b.suggestedFix;
    }

    public static final class Builder {
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

        /**
         * setter
         */
        public Builder setId(final int idNou) {
            this.id = idNou;
            return this;
        }
        /**
         * setter
         */
        public Builder setTitle(final String titleNou) {
            this.title = titleNou;
            return this;
        }
        /**
         * setter
         */
        public Builder setBusinessPriority(final String businessPriorityN) {
            this.businessPriority = businessPriorityN;
            return this;
        }
        /**
         * setter
         */
        public Builder setExpertiseArea(final String experArea) {
            this.expertiseArea = experArea;
            return this;
        }
        /**
         * setter
         */
        public Builder setDescription(final String descrip) {
            this.description = descrip;
            return this;
        }
        /**
         * setter
         */
        public Builder setReportedBy(final String reportBy) {
            this.reportedBy = reportBy;
            return this;
        }
        /**
         * setter
         */
        public Builder setUiElementId(final String elemId) {
            this.uiElementId = elemId;
            return this;
        }
        /**
         * setter
         */
        public Builder setBusinessValue(final String businVal) {
            this.businessValue = businVal;
            return this;
        }
        /**
         * setter
         */
        public Builder setUsabilityScore(final int useScore) {
            this.usabilityScore = useScore;
            return this;
        }
        /**
         * setter
         */
        public Builder setScreenshotUrl(final String url) {
            this.screenshotUrl = url;
            return this;
        }
        /**
         * setter
         */
        public Builder setSuggestedFix(final String fixNou) {
            this.suggestedFix = fixNou;
            return this;
        }
        /**
         * setter
         */
        public Builder setCreatedAt(final String creatAt) {
            this.createdAt = creatAt;
            return this;
        }

        /**
         * @return la obiect efectiv
         */
        public UIFeedbackTicket build() {
            return new UIFeedbackTicket(this);
        }
    }

    // getteri
    public String getUiElementId() {
        return uiElementId;
    }
    public String getBusinessValue() {
        return businessValue;
    }
    public int getUsabilityScore() {
        return usabilityScore;
    }

}
