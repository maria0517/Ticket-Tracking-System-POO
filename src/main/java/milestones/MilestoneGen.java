package milestones;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tickets.Ticket;
import users.*;

import java.time.LocalDate;
import java.util.*;

import static tickets.HistoryOfTicket.actualHist;

public class MilestoneGen {

    private MilestoneGen() { }

    public static void createMilestone(final JsonNode cmd,
        final Map<String, Milestone> allMilestones,  final Map<Integer, Ticket> allTickets,
        final List<User> allUsers, final ObjectNode resultNode, final List<ObjectNode> outputs,
        final String timestamp, final String username, final User util) {
        // trebuie sa verific daca am trecut de perioada de testare
        // nu mai trebuie ; se garanteaza din enunt ca nu se intampla

        String name = cmd.get("name").asText();
        String dueDate = cmd.get("dueDate").asText();
        String numeCreator = cmd.get("username").asText();

        // acum listele
        // aici la tickete trebuie sa verific daca nu cumva au fost puse in alt milestone
        List<Integer> ticketIds = new ArrayList<>();
        boolean validIdTicket = true;
        for (JsonNode idNode : cmd.get("tickets")) {
            if (validIdTicket) {
                List<Milestone> allMilestonesList = new ArrayList<>(allMilestones.values());
                for (Milestone unMil : allMilestonesList) {
                    // il caut sa vad daca exista
                    if (unMil.getTickets().contains(idNode.asInt())) {
                        // il am deja in alta parte
                        // dau mesaj de eroare si ies cu totul
                        validIdTicket = false;
                        resultNode.put("error", "Tickets " + idNode.asInt() + " already"
                                + " assigned" + " to milestone " + unMil.getName() + ".");
                        outputs.add(resultNode);
                    }
                }
                if (validIdTicket) {
                    ticketIds.add(idNode.asInt());
                    // setez pentru fiecare ticket din ce milestone face parte
                    // startTestingPhase nu am asa ceva facut de aia nu merge
                    allTickets.get(idNode.asInt()).setMilName(name);
                    actualHist(allTickets.get(idNode.asInt()), username,
                            timestamp, "createMilestone", "");
                }
            }
        }
        List<String> assignedDevs = new ArrayList<>();
        for (JsonNode dev : cmd.get("assignedDevs")) {
            assignedDevs.add(dev.asText());
        }

        List<String> blockingFor = new ArrayList<>();
        for (JsonNode m : cmd.get("blockingFor")) {
            blockingFor.add(m.asText());
        }

        // verificare id sa vad daca exista toate
        for (int id : ticketIds) {
            if (!allTickets.containsKey(id)) {
                // mesaj de eroare
                break;
            }
        }

        // verif daca developeri alesi sunt din echipa managerului
        Manager managMeu = null;
        User altTip = null;

        // caut mai intai managerul
        for (User u : allUsers) {
            if (u.getUsername().equals(numeCreator) && util.getRole().equals("MANAGER")) {
                // am gasit manager
                managMeu = (Manager) u;
                break;
            } else if (u.getUsername().equals(numeCreator)) {
                // am altceva
                altTip = u;
                break;
            }
        }
        if (managMeu == null) {
            // nu este manager ce am eu acolo sau nu exista pur si simplu
            resultNode.put("error", "The user does not have permission to "
                    + "execute this command: required role "
                    + "MANAGER; user role " + altTip.getRole() + ".");
            outputs.add(resultNode);
        } else {
            // caut in lista lui de subordonati daca exista acel milestone
            for (String devUsername : assignedDevs) {
                if (!managMeu.getSubordinates().contains(devUsername)) {
                    resultNode.put("error", "Developer " + devUsername
                            + " is not part of manager's team.");
                    outputs.add(resultNode);
                    break;
                }
            }
            // daca am trecut de toate astea sunt bine si creez milestone
            if (validIdTicket) {
                Milestone milestone = new Milestone(name, LocalDate.parse(dueDate),
                        blockingFor, assignedDevs, ticketIds, timestamp, username);

                // acum am toti developeri valizi
                // marchez si in listele lor separate
                for (String devUsername : assignedDevs) {
                    for (User u : allUsers) {
                        if (u.getUsername().equals(devUsername)) {
                            // am un developer
                            Developer dev = (Developer) u;
                            dev.addMilName(name);
                            // trebuie sa i adaug si ca observeri
                            milestone.addObserver(dev);
                        }
                    }
                }

                // il pun cu toate milestoneurile
                allMilestones.put(name, milestone);
                // nu afisez nimic la succes
                // trebuie asta ca sa pot face mereu aceste verificari
                milestone.updateBlockState(new ArrayList<>(allMilestones.values()));
                // trebuie sa dau notificare
                milestone.notifyObservers("New milestone " + milestone.getName()
                        + " has been created with due date " + milestone.getDueDate() + ".");
            }
        }
    }
}
