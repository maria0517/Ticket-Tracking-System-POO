package tickets;

public class BugTicket extends Ticket {

	private final String expectedBehavior;
	private final String actualBehavior;
	private final String frequency;
	private final String severity;
	private final String environment;   // optional
	private final Integer errorCode;    // optional

	private BugTicket(Builder b) {
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
	public static class Builder {
		private int id;
		private String title;
		private String businessPriority;
		private String expertiseArea;
		private String description;
		private String reportedBy;

		// BUG-specific
		private String expectedBehavior;
		private String actualBehavior;
		private String frequency;
		private String severity;
		private String environment;
		private Integer errorCode;

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
		public Builder setBusinessPriority(String businessPriority) { this.businessPriority = businessPriority; return this; }
		public Builder setExpertiseArea(String expertiseArea) { this.expertiseArea = expertiseArea; return this; }
		public Builder setDescription(String description) { this.description = description; return this; }
		public Builder setReportedBy(String reportedBy) { this.reportedBy = reportedBy; return this; }

		public Builder setExpectedBehavior(String e) { this.expectedBehavior = e; return this; }
		public Builder setActualBehavior(String a) { this.actualBehavior = a; return this; }
		public Builder setFrequency(String f) { this.frequency = f; return this; }
		public Builder setSeverity(String s) { this.severity = s; return this; }
		public Builder setEnvironment(String env) { this.environment = env; return this; }
		public Builder setErrorCode(Integer code) { this.errorCode = code; return this; }

		public Builder setCreatedAt(String createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		// asta efectiv construieste obiectul meu
		public BugTicket build() {
			return new BugTicket(this);
		}
	}
}
