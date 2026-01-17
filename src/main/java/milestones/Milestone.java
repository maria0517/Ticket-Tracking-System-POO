package milestones;

import forNotifications.Observer;
import forNotifications.Subject;
import tickets.Ticket;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static tickets.Ticket.increasePrior;

public final class Milestone implements Subject {
    // campurile(toate necesare)
    private String name;
    private List<String> blockingFor;
    private LocalDate dueDate; // pt testari si ce mai trebuie le voi face cast la LocalDate
    private List<Integer> tickets;
    private List<String> assignedDevs;
    private String createdAt;
    private String creatorName;

    // astea trebuie setate
    private String status;
    private boolean isBlocked;
    private int daysUntilDue;
    private int overdueBy;
    private List<Integer> openTickets;
    private List<Integer> closedTickets;
    private double completitionPercentage;

    // pt notif
    private List<Observer> assigDev = new ArrayList<>();

    // pt a face increm la business prior
    private int priorityIncrementsDone = 0;
    private String activatedAt;


    public Milestone(final String name, final LocalDate dueDate, final List<String> blockingFor,
            final List<String> assignedDevs, final List<Integer> tickets, final String createdAt,
            final String creatorName) {
        this.name = name;
        this.dueDate = dueDate;
        this.blockingFor = blockingFor;
        this.tickets = tickets;
        this.assignedDevs = assignedDevs;
        this.createdAt = createdAt;
        this.activatedAt = createdAt;
        this.creatorName = creatorName;
        // cand creez milestone -> toate ticketele sunt de tip open
        this.openTickets = new ArrayList<>(tickets);
        this.closedTickets = new ArrayList<>();
        this.completitionPercentage = 0.0;
        this.isBlocked = false;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getAssignedDevs() {
        return assignedDevs;
    }

    public void setAssignedDevs(final List<String> assignedDevs) {
        this.assignedDevs = assignedDevs;
    }

    public List<Integer> getTickets() {
        return tickets;
    }

    public void setTickets(final List<Integer> tickets) {
        this.tickets = tickets;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<String> getBlockingFor() {
        return blockingFor;
    }

    public void setBlockingFor(final List<String> blockingFor) {
        this.blockingFor = blockingFor;
    }

    public String getName() {
        return name;
    }
    public void setName(final String name) {
        this.name = name;
    }

    public String getCreatorName() {
        return creatorName;
    }

    /**
     * pun statusul milestoneului
     */
    // asta e cam doi in unu
    public String getStatus() {
        if (!openTickets.isEmpty()) {
            this.status = "ACTIVE";
        } else {
            // nu mai exista tickete de rezolvat
            this.status = "COMPLETED";
        }
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void  setBlocked(final boolean blocked) {
        isBlocked = blocked;
    }

    /**
     * calculez score de complete al milestoneului
     */
    public double getCompletPerc() {
        this.completitionPercentage = (double) closedTickets.size() / tickets.size();
        completitionPercentage = Math.round(completitionPercentage * 100.0) / 100.0;
        return completitionPercentage;
    }

    public List<Integer> getOpenTickets() {
        return openTickets;
    }

    public List<Integer> getClosedTickets() {
        return closedTickets;
    }

    /**
     * cate zile mai am pana la dueDate
     */
    public int getDaysUntilDue(final String currentTime) {
        // ba nu se face cu atunci cand a fost creat ci cu data comenziii!!!!!
        if (status != null && status.equals("COMPLETED")) {
            return daysUntilDue;
        }
        this.daysUntilDue = (int) ChronoUnit.DAYS.between(LocalDate.parse(currentTime),
                dueDate) + 1;
        if (daysUntilDue <= 0) {
            // s a terminat perioada de rezolvare
            overdueBy = (int) ChronoUnit.DAYS.between(dueDate, LocalDate.parse(currentTime)) + 1;
            return 0;
        }
        return daysUntilDue;
    }

    public void setDaysUntilDue(final int daysUntilDue) {
        this.daysUntilDue = daysUntilDue;
    }

    public int getOverdue() {
        // momentan
        return overdueBy;
    }

    public void setOverdueBy(final int overdueBy) {
        this.overdueBy = overdueBy;
    }

    /**
     * modific starea milestoneului
     */
    public void updateBlockState(final List<Milestone> allMilestones) {
        // merg prin blockingFor a celui curent
        for (String blockedMil : this.blockingFor) {
            for (Milestone m : allMilestones) {
                if (m.getName().equals(blockedMil)) {
                    m.setBlocked(true);
                }
            }
        }
    }

    public int getPriorityIncrementsDone() {
        return priorityIncrementsDone;
    }

    public void setPriorityIncrementsDone(final int priorityIncrementsDone) {
        this.priorityIncrementsDone = priorityIncrementsDone;
    }

    public String getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(final String activatedAt) {
        this.activatedAt = activatedAt;
    }

    @Override
    public void addObserver(final Observer o) {
        assigDev.add(o);
    }

    @Override
    public void notifyObservers(final String message) {
        for (Observer o : assigDev) {
            o.update(message);
        }
    }


    /**
     * functie care imi actualizeaza milestone
     * deblocheaza, sau da notificari pentru cazuri limita
     */
    public static void updateMilestones(final Map<String, Milestone> allMilestones,
             final String timestamp, final Map<Integer, Ticket> allTickets) {
        List<Milestone> allMilList = new ArrayList<>(allMilestones.values());

        for (Milestone m : allMilList) {
            // trebuie sa verific daca nu este complete, caz in care le deblochez pe celelalte
            if (m.getStatus().equals("COMPLETED")) {
                // debloc
                for (String blockedMil : m.getBlockingFor()) {
                    if (!allMilestones.get(blockedMil).isBlocked()) {
                        // nu e blocat deci nu am ce sa mai verific aici
                        break;
                    }
                   // System.out.println("milestone " + allMilestones.get(blockedMil).getName()
                    // + " a fost deblocat la data " + timestamp);
                    allMilestones.get(blockedMil).setBlocked(false);
                    allMilestones.get(blockedMil).setActivatedAt(timestamp);

                    // trebuie sa vad cat a pierdut
                    if (allMilestones.get(blockedMil).getDaysUntilDue(timestamp) == 0) {
                        // a fost deblocat dupa dueDate
                        // notific pe toata lumea
                        allMilestones.get(blockedMil).notifyObservers("Milestone " + blockedMil
                          + " was unblocked after due date. All active tickets are now CRITICAL.");
                    }
                }
            }
        }

        for (Milestone m : allMilestones.values()) {
            // trebuie sa verific daca nu e blocat milestoneul
            // if (!m.isBlocked() && !lastDate.equals(timestamp)) {
            if (!m.isBlocked() && !m.getStatus().equals("COMPLETED")) {
                // pentru fiecare vad cand a fost creat si daca sunt in cazul
                // de doar crestere prioritate sau daca e direct pe critical

                // aici iau de la activatedAt, care e createdAt atunci cand milestone nu a fost
                // blocat niciodata, altfel o iau de cand e activata
                long daysPassed = ChronoUnit.DAYS.between(LocalDate.parse(m.getActivatedAt()),
                        LocalDate.parse(timestamp));

                int incrementsShouldHave = (int) (daysPassed / 3);
                int incrementsToApply = incrementsShouldHave - m.getPriorityIncrementsDone();

                if (incrementsToApply > 0) {
//          System.out.println("sunt aici la data " + timestamp + "pt milestone " + m.getName());
                    for (int i = 0; i < incrementsToApply; i++) {
                        for (Integer ticketId : m.getTickets()) {
                            if (allTickets.containsKey(ticketId)) {
                                Ticket t = allTickets.get(ticketId);
                                if (!t.getStatus().equals("CLOSED")) {
                                    increasePrior(t);
                                }
                            }
                        }
                    }
                    m.setPriorityIncrementsDone(incrementsShouldHave);
                }
                if (LocalDate.parse(timestamp).isEqual(m.dueDate.minusDays(1))) {
                    // asta e mai grava; toate ticketele trebuie facute critical
                    boolean tickUnresolved = false;

                    for (Integer ticketId : m.getTickets()) {
                        if (allTickets.containsKey(ticketId)) {
                            Ticket t = allTickets.get(ticketId);
                            if (!t.getStatus().equals("CLOSED")) {
                                t.setBussinesPriority("CRITICAL");
                                tickUnresolved = true;
                            }
                        }
                    }
                    // daca am un ticket nerezolvat
                    // dau eroare
                    if (tickUnresolved) {
                        m.notifyObservers("Milestone " + m.getName() + " is due tomorrow."
                                 + " All unresolved tickets are now CRITICAL.");
                    }
                }
            }
        }
    }
}
