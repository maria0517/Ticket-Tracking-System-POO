package tickets;

public class HistoryOfTicket {
    private String author;
    private String timestamp;
    // la assign / deassign; la createMil, la change status
    private String action;

    // opțional:
    String fromStatus;
    String toStatus;
    String milName;
    String fromDev;

    public HistoryOfTicket(String author, String timestamp, String action) {
        this.author = author;
        this.timestamp = timestamp;
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public String getAuthor() {
        return author;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static void actualHist(Ticket tick, String author, String timestamp, String action, String oldStatus) {
        // acum vad cum il aduag aici fara probleme
        HistoryOfTicket forAdd = null;
        if (action.equals("createMilestone")) {
            // trebuie sa mai pun numele milestone + manager
            forAdd = new HistoryOfTicket(author, timestamp, "ADDED_TO_MILESTONE");
            forAdd.setMilName(tick.getMilName());
            // si cam asta e
        }
        if (action.equals("assignTicket")) {
            forAdd = new HistoryOfTicket(author, timestamp, "ASSIGNED");
        }
        if (action.equals("undoAssignTicket")) {
            forAdd = new HistoryOfTicket(author, timestamp, "DE-ASSIGNED");
        }
        if (action.equals("changeStatus") || action.equals("undoChangeStatus")) {
            forAdd = new HistoryOfTicket(author, timestamp, "STATUS_CHANGED");
            forAdd.setFromStatus(oldStatus);
            forAdd.setToStatus(tick.getStatus());
        }
        // mai e aia cu removeDev care nu stiu cat imi trebuie acum
        // il adaug
        tick.getHistory().add(forAdd);
    }

    // momentan setteri, ajung si la getteri
    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public void setMilName(String milName) {
        this.milName = milName;
    }

    public void setFromDev(String fromDev) {
        this.fromDev = fromDev;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public String getMilName() {
        return milName;
    }

    public String getFromDev() {
        return fromDev;
    }
}
