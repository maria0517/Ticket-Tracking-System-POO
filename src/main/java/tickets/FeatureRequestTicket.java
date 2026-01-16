package tickets;

public final class FeatureRequestTicket extends Ticket {

    // aici nu ar mai fi nevoie de ticket
    private String businessValue;
    private String customerDemand;

    // constructor complet
    private FeatureRequestTicket(final Builder b) {
        super(b.id, "FEATURE_REQUEST", b.title, b.businessPriority,
           "OPEN", b.expertiseArea, b.description, b.reportedBy, b.createdAt);

        this.businessValue = b.businessValue;
        this.customerDemand = b.customerDemand;
    }

    // getteri
    public String getBusinessValue() {
        return businessValue;
    }

    public String getCustomerDemand() {
        return customerDemand;
    }

    // builderul
    public static final class Builder {
        // campuri (comune + particulare)
        private int id;
        private String title;
        private String businessPriority;
        private String expertiseArea;
        private String description;
        private String reportedBy;

        private String businessValue;
        private String customerDemand;
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
        public Builder setBusinessValue(final String businessVal) {
            this.businessValue = businessVal;
            return this;
        }
        /**
         * setter
         */
        public Builder setCustomerDemand(final String customerDemandN) {
            this.customerDemand = customerDemandN;
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
         * aici se face buildul final
         */
        public FeatureRequestTicket build() {
            return new FeatureRequestTicket(this);
        }
    }
}
