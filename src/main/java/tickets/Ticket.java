package tickets;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public abstract class Ticket {
	// campuri comune tuturor tichetelor
	protected int id;
	protected String type;
	protected String title;
	protected String businessPriority;
	protected String status;
	protected String expertiseArea;
	protected String description;
	protected String reportedBy;

	// urmeaza niste atribute care se instantieaza cu nimic la inceput
	protected String createdAt;
	protected String assignedAt;
	protected String solvedAt;
	protected String assignedTo;
	protected List<Comment> comments;

	String milestoneName;
	List <HistoryOfTicket> history;

	// consctructor simplu
	// o sa am buildere pentru fiecare subtip de tichet;
	// ma folosesc de asta
	public Ticket(int id, String type, String title, String businessPriority,
		String status, String expertiseArea, String description, String reportedBy, String createdAt) {
		this.id = id;
		this.type = type;
		this.title = title;
		this.businessPriority = businessPriority;
		this.status = status;
		this.expertiseArea = expertiseArea;
		this.description = description;
		this.reportedBy = reportedBy;
		this.createdAt = createdAt;
		this.assignedAt = "";
		this.solvedAt = "";
		this.assignedTo = "";
		this.comments = new ArrayList<>();
		this.milestoneName = "";
		this.history = new ArrayList<>();
	}

	// getteri pentru toata lumea
	public int getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public String getTitle() {
		return title;
	}
	public String getBusinessPriority() {
		return businessPriority;
	}
	public String getStatus() {
		return status;
	}
	public String getExpertiseArea() {
		return expertiseArea;
	}
	public String getDescription() {
		return description;
	}
	public String getReportedBy() {
		return reportedBy;
	}
	public String getCreatedAt() { return createdAt; }
	public String getAssignedAt() { return assignedAt; }
	public String getSolvedAt() { return solvedAt; }
	public String getAssignedTo() { return assignedTo; }
	public List<HistoryOfTicket>  getHistory() { return history; }

	public void setStatus(String status) {
		this.status = status;
	}

	public void setBussinesPriority(String bussinesPriority) {
		this.businessPriority = bussinesPriority;
	}

	public void setAssignedAt(String timestamp) {
		this.assignedAt = timestamp;
	}

	public void setSolvedAt(String timestamp) {
		this.solvedAt = timestamp;
	}

	public void setMilName(String name) {
		this.milestoneName = name;
	}

	public String getMilName() {
		return milestoneName;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public double getDaysToResolve() {
		LocalDate assignedDate = LocalDate.parse(assignedAt);
		LocalDate solvedDate = LocalDate.parse(solvedAt);

		// intrebarea este cat scoate nebunia asta daca se assigned = solved
		return (double) ChronoUnit.DAYS.between(assignedDate, solvedDate) + 1.0;
	}
}
