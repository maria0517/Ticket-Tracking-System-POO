package tickets;

public class FeatureRequestTicket extends Ticket {

    // aici nu ar mai fi nevoie de ticket
    private String businessValue;
    private String customerDemand;

    // constructor complet
    private FeatureRequestTicket(Builder b) {
        super( b.id, "FEATURE_REQUEST", b.title, b.businessPriority,
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
    public static class Builder {
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


        // setteri
        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setBusinessPriority(String businessPriority) {
            this.businessPriority = businessPriority;
            return this;
        }

        public Builder setExpertiseArea(String expertiseArea) {
            this.expertiseArea = expertiseArea;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setReportedBy(String reportedBy) {
            this.reportedBy = reportedBy;
            return this;
        }

        public Builder setBusinessValue(String businessValue) {
            this.businessValue = businessValue;
            return this;
        }

        public Builder setCustomerDemand(String customerDemand) {
            this.customerDemand = customerDemand;
            return this;
        }

        public Builder setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        // build final
        public FeatureRequestTicket build() {
            return new FeatureRequestTicket(this);
        }
    }
}
