package tickets;

public final class HistoryOfTicket {
    private String author;
    private String timestamp;
    // la assign / deassign; la createMil, la change status
    private String action;

    // optionale astea
    private String fromStatus;
    private String toStatus;
    private String milName;

    public HistoryOfTicket(final String author, final String timestamp, final String action) {
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

    /**
     * adaug comentariu la tichet
     */
    public static void actualHist(final Ticket tick, final String author, final String timestamp,
           final String action, final String oldStatus) {
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
        // il adaug
        tick.getHistory().add(forAdd);
    }

    // momentan setteri, ajung si la getteri
    public void setFromStatus(final String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public void setToStatus(final String toStatus) {
        this.toStatus = toStatus;
    }

    public void setMilName(final String milName) {
        this.milName = milName;
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

}
