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

    private String milestoneName;
    private List<HistoryOfTicket> history;

    // consctructor simplu
    // o sa am buildere pentru fiecare subtip de tichet
    // ma folosesc de asta
    public Ticket(final int id, final String type, final String title,
          final String businessPriority, final String status, final String expertiseArea,
          final String description, final String reportedBy, final String createdAt) {
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
    public final int getId() {
        return id;
    }
    public final String getType() {
        return type;
    }
    public final String getTitle() {
        return title;
    }
    public final String getBusinessPriority() {
        return businessPriority;
    }
    public final String getStatus() {
        return status;
    }
    public final String getExpertiseArea() {
        return expertiseArea;
    }
    public final String getDescription() {
         return description;
    }
    public final String getReportedBy() {
         return reportedBy;
    }
    public final String getCreatedAt() {
        return createdAt;
    }
    public final String getAssignedAt() {
        return assignedAt;
    }
    public final String getSolvedAt() {
        return solvedAt;
    }
    public final String getAssignedTo() {
        return assignedTo;
    }
    public final List<HistoryOfTicket> getHistory() {
        return history;
    }

    public final void setStatus(final String status) {
        this.status = status;
    }

    public final void setBussinesPriority(final String bussinesPriority) {
        this.businessPriority = bussinesPriority;
    }

    public final void setAssignedAt(final String timestamp) {
        this.assignedAt = timestamp;
    }

    public final void setSolvedAt(final String timestamp) {
        this.solvedAt = timestamp;
    }

    public final void setMilName(final String name) {
        this.milestoneName = name;
    }

    public final String getMilName() {
        return milestoneName;
    }

    public final List<Comment> getComments() {
        return comments;
    }

    /**
     * cat a durat sa fie rezolvat un tichet
     */
    public double getDaysToResolve() {
        LocalDate assignedDate = LocalDate.parse(assignedAt);
        LocalDate solvedDate = LocalDate.parse(solvedAt);

        // intrebarea este cat scoate chestia asta daca assigned = solved
        return (double) ChronoUnit.DAYS.between(assignedDate, solvedDate) + 1.0;
    }

    public final void setAssignedtTo(final String username) {
        this.assignedTo = username;
    }

    /**
     * creste prior ticket primit ca parametru
     */
    public static void increasePrior(final Ticket t) {
        // cresc prioritatea la ticket
        switch (t.getBusinessPriority()) {
            case "LOW":
                t.setBussinesPriority("MEDIUM");
                break;
            case "MEDIUM":
                t.setBussinesPriority("HIGH");
                break;
            case "HIGH":
                t.setBussinesPriority("CRITICAL");
                break;
            default:
                break;
        }
    }
}
