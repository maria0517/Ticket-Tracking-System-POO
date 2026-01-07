package users;

import forNotifications.Observer;
import main.Milestone;
import tickets.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Developer extends User implements Observer {
    private String hireDate;
    private String expertiseArea;
    private String seniority;

    // mi ar trebui aici ticketele asignate
    private List<Ticket> assignedTickets;
    private List<Ticket> pastAssignedTickets;
    private List<String> milName;

    // notificar
    private List<String> notif;

    @Override
    public void update(String message) {
        notif.add(message);
    }

    // constructor
    public Developer(String username, String email, String hireDate,
               String expertiseArea, String seniority) {
        super(username, email, "DEVELOPER");
        this.hireDate = hireDate;
        this.expertiseArea = expertiseArea;
        this.seniority = seniority;
        // momentan la 0 pana mai departe
        this.assignedTickets = new ArrayList<>();
        this.pastAssignedTickets = new ArrayList<>();
        this.milName = new ArrayList<>();
        this.notif = new ArrayList<>();
    }

    // trebuie pentru atunci cand rezolv tichete
    // trebuie specializare + vechime
    public String getHireDate() {
        return hireDate;
    }
    public String getExpertiseArea() {
        return expertiseArea;
    }
    public String getSeniority() {
        return seniority;
    }

    public void addTickets(Ticket unTicket) {
        assignedTickets.add(unTicket);
        // trebuie facute si modificari la ticket
    }

    public List<Ticket> getAssignedTckets() {
        return  assignedTickets;
    }

    public  List<Ticket> getPastAssigTickets() {
        return  pastAssignedTickets;
    }

    public void unAssigTick(Ticket unTick) {
        assignedTickets.remove(unTick);
        // aici trebuie sa l adaug in lista de pastTickets
        pastAssignedTickets.add(unTick);
    }

    public List<String> getMilName() {
        return milName;
    }

    public void addMilName(String milName) {
       this.milName.add(milName);
    }

    public List<String> getNotifications() {
        return notif;
    }

    // imi trebuie dupa ce le afisez
    public void clearNotifications() {
        notif.clear();
    }

    // ticketul x poate fi rezolvat de developeri din
    // zonele de expertiza a, b, c
    private List<String> allowedAreas(String expertiseAreaEx) {
        List<String> areas = new ArrayList<>();

        switch (expertiseAreaEx) {
            case "FRONTEND":
                areas.add("DESIGN");
                areas.add("FRONTEND");
                areas.add("FULLSTACK");
                break;

            case "BACKEND":
                areas.add("BACKEND");
                areas.add("FULLSTACK");
                break;

            case "DEVOPS":
                areas.add("DEVOPS");
                areas.add("FULLSTACK");
                break;

            case "DESIGN":
                areas.add("DESIGN");
                areas.add("FRONTEND");
                areas.add("FULLSTACK");
                break;

            case "DB":
                areas.add("BACKEND");
                areas.add("DB");
                areas.add("FULLSTACK");
                break;
        }

        return areas;
    }


    public String validAssigment(Ticket unTick, Map<String, Milestone> allMilestones) {
        // verifc 1 - zona de expertiza
        // aici vad ce poate sa faca developerul meu (ce zone)
        List<String> allowedAreasForTicket = allowedAreas(unTick.getExpertiseArea());
        if (!allowedAreasForTicket.contains(expertiseArea)) {
            String errorMessage = "Developer " + username + " cannot assign ticket " + unTick.getId()
                    + " due to expertise area. Required: "
                    + String.join(", ", allowedAreasForTicket)
                    + "; Current: " + expertiseArea + ".";

            return errorMessage;
        }
        // fulstack e cel mai seic (face de toate)

        // verif 2 - senioritate
        if(this.seniority.equals("JUNIOR") && !(unTick.getBusinessPriority().
              equals("LOW") || (unTick.getBusinessPriority().equals("MEDIUM")))) {
            return "Developer " + username + " cannot assign ticket " + unTick.getId() + " due to seniority level. "
            + "Required: MID, SENIOR; Current: " + this.seniority + ".";
        }
        if(this.seniority.equals("MID") && unTick.getBusinessPriority().equals("CRITICAL")) {
            return  "Developer " + username + " cannot assign ticket " + unTick.getId() + " due to seniority level. "
                    + "Required: SENIOR; Current: " + this.seniority + ".";
        }
        // daca e senior poate orice si nu am treaba cu el

        // verif 3 - open la ticket
        if (!unTick.getStatus().equals("OPEN")) {
            return "Only OPEN tickets can be assigned.";
        }


        // pe partea cu milestones ma mai gandesc ca e ceva fishy acolo; trebuie exemple
        if (!milName.contains(unTick.getMilName())) {
            // dev nu face parte din acel milestone
            return "Developer " + username + " is not assigned"
                + " to milestone " + unTick.getMilName() + ".";
        }

        if (allMilestones.get(unTick.getMilName()).isBlocked()) {
            return "Cannot assign ticket " + unTick.getId() + " from blocked milestone "
                 + unTick.getMilName() + ".";
        }

        return "valid";
    }
}

