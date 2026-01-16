package tickets;

public final class BugTicket extends Ticket {

    private final String expectedBehavior;
    private final String actualBehavior;
    private final String frequency;
    private final String severity;
    private final String environment;   // optional
    private final Integer errorCode;    // optional

    private BugTicket(final Builder b) {
        // iau de la ticket atributele comune
        super(b.id, "BUG", b.title, b.businessPriority,
                "OPEN", b.expertiseArea, b.description, b.reportedBy, b.createdAt);
        // apoi cele specifice
        this.expectedBehavior = b.expectedBehavior;
        this.actualBehavior = b.actualBehavior;
        this.frequency = b.frequency;
        this.severity = b.severity;
        this.environment = b.environment;
        this.errorCode = b.errorCode;
    }

    // builderul specific
    public static final class Builder {
        private int id;
        private String title;
        private String businessPriority;
        private String expertiseArea;
        private String description;
        private String reportedBy;

        // bug specific
        private String expectedBehavior;
        private String actualBehavior;
        private String frequency;
        private String severity;
        private String environment;
        private Integer errorCode;

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
        public Builder setExpertiseArea(final String expertiseAreaN) {
            this.expertiseArea = expertiseAreaN;
            return this;
        }
        /**
         * setter
         */
        public Builder setDescription(final String descriptionN) {
            this.description = descriptionN;
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
        public Builder setExpectedBehavior(final String eNou) {
            this.expectedBehavior = eNou;
            return this;
        }
        /**
         * setter
         */
        public Builder setActualBehavior(final String actBehav) {
            this.actualBehavior = actBehav;
            return this;
        }
        /**
         * setter
         */
        public Builder setFrequency(final String freq) {
            this.frequency = freq;
            return this;
        }
        /**
         * setter
         */
        public Builder setSeverity(final String sev) {
            this.severity = sev;
            return this;
        }
        /**
         * setter
         */
        public Builder setEnvironment(final String env) {
            this.environment = env;
            return this;
        }
        /**
         * setter
         */
        public Builder setErrorCode(final Integer code) {
            this.errorCode = code;
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
         * asta efectiv construieste obiectul meu
         */
        public BugTicket build() {
            return new BugTicket(this);
        }
    }

    public String getFrequency() {
        return frequency;
    }

    public String getSeverity() {
        return severity;
    }
}
