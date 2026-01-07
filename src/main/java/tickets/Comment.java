package tickets;

import users.Developer;
import users.User;

public class Comment {
    private String author;
    private String message;
    private String createdAt;

    public Comment(String author, String content, String createdAt) {
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

    public static void addComment(User util, Ticket tick, String comment, String timestamp) {
        // adaug comentariul efectiv
        Comment newComm = new Comment(util.getUsername(), comment, timestamp);
        tick.getComments().add(newComm);
    }

    public static String undoAddComment(User util,Ticket tick) {
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

    public static String validAddComm(User util, Ticket tick, String comment) {
        // aici toate verificarile

        if (tick != null) {
            // verif anonimicitate
            if (tick.getReportedBy().isEmpty()) {
                return "Comments are not allowed on anonymous tickets.";
            }
            if (util.getRole().equals("REPORTER") && tick.getStatus().equals("CLOSED")) {
                return "Reporters cannot comment on CLOSED tickets.";
            }
            if (comment.length() < 10) {
                return "Comment must be at least 10 characters long.";
            }
            if (util.getRole().equals("DEVELOPER")) {
                Developer developer = (Developer) util;
                if (!developer.getAssignedTckets().contains(tick)) {
                    // nu e bun
                    // incearca sa comenteze la un tichet care nu e al lui
                    return "Ticket " + tick.getId() + " is not assigned to the developer " + util.getUsername() + ".";
                }
            }
            if (util.getRole().equals("REPORTER") && !tick.getReportedBy().equals(util.getUsername())) {
                return "Reporter " + util.getUsername() + " cannot comment on ticket " + tick.getId() + ".";
            }

            // daca a ajuns pana aici e ok
            return "valid";
        }
        return "ignore";
    }
}
