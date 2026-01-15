package main;

import fornotifications.Observer;
import fornotifications.Subject;
import tickets.Ticket;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Milestone implements Subject {
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


    public Milestone(String name, LocalDate dueDate, List<String> blockingFor,
             List<String> assignedDevs, List<Integer> tickets, String createdAt, String creatorName) {
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

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getAssignedDevs() {
        return assignedDevs;
    }

    public void setAssignedDevs(List<String> assignedDevs) {
        this.assignedDevs = assignedDevs;
    }

    public List<Integer> getTickets() {
        return tickets;
    }

    public void setTickets(List<Integer> tickets) {
        this.tickets = tickets;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<String> getBlockingFor() {
        return blockingFor;
    }

    public void setBlockingFor(List<String> blockingFor) {
        this.blockingFor = blockingFor;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getCreatorName() {
        return creatorName;
    }

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

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void  setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

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

    public int getDaysUntilDue(String currentTime) {
        // ba nu se face cu atunci cand a fost creat ci cu data comenziii!!!!!
        if (status != null && status.equals("COMPLETED")) {
            return daysUntilDue;
        }
        this.daysUntilDue = (int) ChronoUnit.DAYS.between(LocalDate.parse(currentTime), dueDate) + 1;
        if (daysUntilDue <= 0) {
            // s a terminat perioada de rezolvare
            overdueBy = (int) ChronoUnit.DAYS.between(dueDate, LocalDate.parse(currentTime)) + 1;
            return 0;
        }
        return daysUntilDue;
    }

    public void setDaysUntilDue(int daysUntilDue) {
        this.daysUntilDue = daysUntilDue;
    }

    public int getOverdue() {
        // momentan
        return overdueBy;
    }

    public void setOverdueBy(int overdueBy) {
        this.overdueBy = overdueBy;
    }

    public void updateBlockState(List<Milestone> allMilestones) {
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

    public void setPriorityIncrementsDone(int priorityIncrementsDone) {
        this.priorityIncrementsDone = priorityIncrementsDone;
    }

    public String getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(String activatedAt) {
        this.activatedAt = activatedAt;
    }

    @Override
    public void addObserver(Observer o) {
        assigDev.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        assigDev.remove(o);
    }

    @Override
    public void notifyObservers(String message) {
        for (Observer o : assigDev) {
            o.update(message);
        }
    }

    public static void increasePriority(Ticket t) {
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
        }
    }

    public static void updateMilestones(Map<String, Milestone> allMilestones, String timestamp,  Map<Integer, Ticket> allTickets, String comm) {
        List<Milestone> allMilList = new ArrayList<>(allMilestones.values());

        for(Milestone m : allMilList) {
            // trebuie sa verific daca nu este complete, caz in care le deblochez pe celelalte
            if (m.getStatus().equals("COMPLETED")) {
                // debloc
                for (String blockedMil : m.getBlockingFor()) {
                    if (!allMilestones.get(blockedMil).isBlocked()) {
                        // nu e blocat deci nu am ce sa mai verific aici
                        break;
                    }
                    System.out.println("milestone " + allMilestones.get(blockedMil).getName() + " a fost deblocat la data " + timestamp);
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

        for(Milestone m : allMilestones.values()) {
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
//                    System.out.println("sunt aici la data " + timestamp + "pt milestone " + m.getName());
                    for (int i = 0; i < incrementsToApply; i++) {
                        for (Integer ticketId : m.getTickets()) {
                            if (allTickets.containsKey(ticketId)) {
                                Ticket t = allTickets.get(ticketId);
                                if (!t.getStatus().equals("CLOSED")) {
                                    increasePriority(t);
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
                        m.notifyObservers( "Milestone " + m.getName() + " is due tomorrow." +
                                  " All unresolved tickets are now CRITICAL.");
                    }
                }
            }
        }
    }
}
