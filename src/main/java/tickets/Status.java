package tickets;

import main.Milestone;
import users.Developer;

import java.util.Map;

public final class Status {

    private Status() {
        // pt (ne)instantiere
    }

    /**
     * metoda care imi schimba statusul tichetelor si actualizeaza
     * milestone in cazuri limita (nu mai exista tichete open)
     */
    public static String changingStatus(final Developer dev, final String timestamp,
        final Map<String, Milestone> allMilestone, final Ticket ticket, final int changeType) {
        // incep sa vad cum ma duc
        Milestone m = allMilestone.get(ticket.getMilName());
        if (changeType == 1) {
            // +1
            if (ticket.getStatus().equals("IN_PROGRESS")) {
                ticket.setStatus("RESOLVED");
                // trebuie setata data la ticket, cand s a finalizat
                ticket.setSolvedAt(timestamp);
            } else if (ticket.getStatus().equals("RESOLVED")) {
                ticket.setStatus("CLOSED");
                // trebuie sa il mut din openTickets in closedTickets
                m.getOpenTickets().remove(Integer.valueOf(ticket.getId()));
                m.getClosedTickets().add(ticket.getId());
                // trebuie sa vad daca e ultimul closed, caz
                // in care trebuie sa deblochez celelalte milestoneuri
                // fac cand dau updateMilestone
                if (m.getOpenTickets().size() == 0) {
                    // trebuie sa blochez dueDate si overdueBy
                    m.setDaysUntilDue(m.getDaysUntilDue(timestamp));
                    m.setOverdueBy(m.getOverdue());
                }
            }
        } else {
            // -1
            if (ticket.getStatus().equals("RESOLVED")) {
                ticket.setStatus("IN_PROGRESS");
            }
            // nu stiu daca un closed se mai poate intoarce
            // ipotetic nu ar trebui sa mai poata
        }
        return "valid";
    }
}
