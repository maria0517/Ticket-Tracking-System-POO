package tickets;

import users.Developer;
import users.User;

public final class Comment {
    private static final int MIN_COMMENT_LENGTH = 10; // aici e rezolvarea

    private String author;
    private String message;
    private String createdAt;

    public Comment(final String author, final String content, final String createdAt) {
        this.author = author;
        this.message = content;
        this.createdAt = createdAt;
    }

    public String getAuthor() {
        return author;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getMessage() {
        return message;
    }

    /**
     * adauga un comm (string) in lista fiecarui ticket
     */
    public static void addComment(final User util, final Ticket tick,
               final String comment, final String timestamp) {
        // adaug comentariul efectiv
        Comment newComm = new Comment(util.getUsername(), comment, timestamp);
        tick.getComments().add(newComm);
    }
    /**
     * sterge un comm in lista fiecarui ticket
     */
    public static String undoAddComment(final User util, final Ticket tick) {
        // aici trebuie sa verific daca nu cumva e anonim ticketul
        // caz in care nu fac nimic
        if (tick != null) {
            if (tick.getReportedBy().isEmpty()) {
                // nu i bun
                return "naspa";
            }
            // in rest sunt ok
            // caut ultimul comentariu al userului (de la final)
            for (int i = tick.getComments().size() - 1; i >= 0; i--) {
                Comment c = tick.getComments().get(i);

                if (c.author.equals(util.getUsername())) {
                    tick.getComments().remove(i);
                    break;
                }
            }
            return "valid";
        }
        return "ignore";
    }

    /**
     * functie de validare
     * returneaza mesaj de eroare sau "valid"
     */
    public static String validAddComm(final User util, final Ticket tick, final String comment) {
        // aici toate verificarile

        if (tick != null) {
            // verif anonimicitate
            if (tick.getReportedBy().isEmpty()) {
                return "Comments are not allowed on anonymous tickets.";
            }
            if (util.getRole().equals("REPORTER") && tick.getStatus().equals("CLOSED")) {
                return "Reporters cannot comment on CLOSED tickets.";
            }
            if (comment.length() < MIN_COMMENT_LENGTH) {
                return "Comment must be at least 10 characters long.";
            }
            if (util.getRole().equals("DEVELOPER")) {
                Developer developer = (Developer) util;
                if (!developer.getAssignedTckets().contains(tick)) {
                    // nu e bun
                    // incearca sa comenteze la un tichet care nu e al lui
                    return "Ticket " + tick.getId() + " is not assigned "
                           + "to the developer " + util.getUsername() + ".";
                }
            }
            if (util.getRole().equals("REPORTER")
                    && !tick.getReportedBy().equals(util.getUsername())) {
                return "Reporter " + util.getUsername() + " cannot"
                        + " comment on ticket " + tick.getId() + ".";
            }

            // daca a ajuns pana aici e ok
            return "valid";
        }
        return "ignore";
    }
}
