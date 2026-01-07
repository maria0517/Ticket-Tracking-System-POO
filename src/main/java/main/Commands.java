package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tickets.BugTicket;
import tickets.FeatureRequestTicket;
import tickets.Ticket;
import tickets.UIFeedbackTicket;
import users.Developer;
import users.Manager;
import users.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.ViewTickets.*;
import static tickets.Comment.*;
import static main.Milestone.updateMilestones;
import static main.viewMilestones.viewGoodMilestones;
import static tickets.HistoryOfTicket.actualHist;
import static tickets.Status.changingStatus;


public class Commands {
    // lista pentru comm si mapper pentru formatare output
    private final List<JsonNode> commands;
    private final ObjectMapper mapper = new ObjectMapper();

    // constructorul
    public Commands(List<JsonNode> commands) {
        this.commands = commands;
    }

    // lista de tichete
    private final Map<Integer, Ticket> allTickets = new HashMap<>();

    private final Map<String, Milestone> allMilestones = new HashMap<>();

    // data pentru a putea vedea perioada de testing
    boolean firstComm = false;
    private String testPeriod = null;
    private String testPerEnd = null;
    // 12 zile pentru testare


    // efectiv parcurgere comenzi si procesarea lor
    public void prelucComm(List<ObjectNode> outputs, List<User> allUsers) {
        // imi trebuie o lista de tickete si lista de utilizatori
        for (JsonNode cmd : commands) {
            String command = cmd.get("command").asText();
            String username = cmd.get("username").asText();
            User util = null;
            // imi trebuie peste tot
            for (User u : allUsers) {
                if (u.getUsername().equals(username)) {
                    util = u;
                }
            }
            String timestamp = cmd.get("timestamp").asText();
            JsonNode params = cmd.get("params");
            ObjectNode resultNode =  mapper.createObjectNode();
            if (!firstComm) {
                firstComm = true;
                // acum trebuie sa iau timpul de start
                testPeriod = timestamp;
                // am timpul de start; trebuie cel de final
                LocalDate start = LocalDate.parse(testPeriod);
                testPerEnd = start.plusDays(12).toString();
            }

            // fac verificari daca trebuie sa modific stare tickete
            updateMilestones(new ArrayList<>(allMilestones.values()), timestamp, allTickets, command);

            // creare output; daca am
            if (command.equals("viewTickets")) {
                resultNode.put("command", command);
                resultNode.put("username", username);
                resultNode.put("timestamp", timestamp);
                resultNode.put("tickets", getVisibleTickets(util, new ArrayList<>(allTickets.values())));
                outputs.add(resultNode);
            }

            // trebuie sa fac logica de validare tickete in functie de perioada testare
            // aici incep sa vad ce tip de comanda am
            if (command.equals("reportTicket")) {
                // aici verific daca nu cumva ticketul nu mai este in perioada de testare
                LocalDate ticketDate = LocalDate.parse(timestamp);
                LocalDate start = LocalDate.parse(testPeriod);
                LocalDate end = LocalDate.parse(testPerEnd);

                // verific daca nu am trecut
                if (ticketDate.isAfter(end)) {
                    resultNode.put("command", command);
                    resultNode.put("username", username);
                    resultNode.put("timestamp", timestamp);
                    resultNode.put("error", "Tickets can only be reported during testing phases.");
                    outputs.add(resultNode);
                    continue; // nu mai creez ticketul
                }

                // trebuie creat un ticket de un anumit tip
                // nu returnez nimic; doar erori
                // o eroare -> userul care a facut ticketul nu exista
                User reporter = null;
                boolean exista = false;
                for (User user : allUsers) {
                    // verific daca exista
                    if (user.getUsername().equals(username)) {
                        // exista ii dau cu true si ies de aici
                        reporter = user;
                        exista = true;
                        break;
                    }
                }
                if (!exista) {
                    // nu am userul; dau eroare
                    resultNode.put("command", command);
                    resultNode.put("username", username);
                    resultNode.put("timestamp", timestamp);
                    resultNode.put("error", "The user " + username + " does not exist.");
                    outputs.add(resultNode);
                }
                if (exista) {
                    // trebuie creat ticketul
                    String ticketType = params.get("type").asText();
                    String title = params.get("title").asText();
                    String businessPriority = params.get("businessPriority").asText();
                    String expertiseArea = params.get("expertiseArea").asText();
                    String description = null; //presupun ca nu am
                    if (params.get("description") != null) {
                        description = params.get("description").asText();
                    }
                    String reportedBy = null;
                    if (params.get("reportedBy") != null) {
                        reportedBy = params.get("reportedBy").asText();
                    }

                    Ticket newTicket = null;

                    int newId = allTickets.size();

                    if (ticketType.equals("FEATURE_REQUEST")) {
                        // specifice
                        String businessValue = params.get("businessValue").asText();
                        String customerDemand = params.get("customerDemand").asText();
                        // vad daca a fost facut cumva anonim ticketul ca nu e bine
                        if (!reportedBy.isEmpty()) {
                            // apel builder
                            newTicket = new FeatureRequestTicket.Builder().setId(newId).setTitle(title)
                                    .setBusinessPriority(businessPriority).setExpertiseArea(expertiseArea)
                                    .setDescription(description).setReportedBy(reportedBy)
                                    .setBusinessValue(businessValue).setCustomerDemand(customerDemand)
                                    .setCreatedAt(timestamp).build();
                        } else {
                            // nu e raportat de nimeni; nu se poate
                            resultNode.put("command", command);
                            resultNode.put("username", username);
                            resultNode.put("timestamp", timestamp);
                            resultNode.put("error", "Anonymous reports are "
                                    + "only allowed for tickets of type BUG.");
                            outputs.add(resultNode);
                        }
                    } else if (ticketType.equals("BUG")) {
                        // cele specifice
                        String expectedBehavior = params.get("expectedBehavior").asText();
                        String actualBehavior =  params.get("actualBehavior").asText();
                        String frequency =  params.get("frequency").asText();
                        String severity = params.get("severity").asText();
                        String environment = null;
                        if (params.get("enviroment") != null) {
                            environment = params.get("enviroment").asText();
                        }
                        Integer errorCode = null;
                        if (params.get("errorCode") != null) {
                            errorCode = Integer.parseInt(params.get("errorCode").asText());
                        }
                        // daca am ticket anonim, prioritate e LOW
                        if (reportedBy.isEmpty()) {
                            businessPriority = "LOW";
                        }

                        newTicket = new BugTicket.Builder().setId(newId).setTitle(title)
                                .setBusinessPriority(businessPriority).setExpertiseArea(expertiseArea)
                                .setDescription(description).setReportedBy(reportedBy).
                                setExpertiseArea(expertiseArea).setDescription(description).
                                setExpectedBehavior(expectedBehavior).setActualBehavior(actualBehavior)
                                .setFrequency(frequency).setSeverity(severity).setEnvironment(environment)
                                .setCreatedAt(timestamp).setErrorCode(errorCode).build();

                    } else if (ticketType.equals("UI_FEEDBACK")) {
                        String uiElementId = null;
                        if (params.get("uiElementId") != null) {
                            uiElementId = params.get("uiElementId").asText();
                        }
                        String businessValue =  params.get("businessValue").asText();
                        int usabilityScore =   Integer.parseInt(params.get("usabilityScore").asText());
                        // optionale
                        String screenshotUrl = null;
                        if (params.get("screenshotUrl") != null) {
                            screenshotUrl = params.get("screenshotUrl").asText();
                        }
                        String suggestedFix = null;
                        if (params.get("suggestedFix") != null) {
                            suggestedFix = params.get("suggestedFix").asText();
                        }

                        if (!reportedBy.isEmpty()) {
                            // apel builder
                            newTicket = new UIFeedbackTicket.Builder().setId(newId).setTitle(title)
                                    .setBusinessPriority(businessPriority).setExpertiseArea(expertiseArea)
                                    .setDescription(description).setReportedBy(reportedBy).
                                    setExpertiseArea(expertiseArea).setDescription(description).
                                    setUiElementId(uiElementId).setBusinessValue(businessValue)
                                    .setUsabilityScore(usabilityScore).setScreenshotUrl(screenshotUrl)
                                    .setSuggestedFix(suggestedFix).setCreatedAt(timestamp).build();;
                        } else {
                            // nu e raportat de nimeni; nu se poate
                            resultNode.put("command", command);
                            resultNode.put("username", username);
                            resultNode.put("timestamp", timestamp);
                            resultNode.put("error", "Anonymous reports are "
                                    + "only allowed for tickets of type BUG.");
                            outputs.add(resultNode);
                        }
                    }
                    // adaug daca exista ceva
                    if (newTicket != null) {
                        allTickets.put(newId, newTicket);
                    }
                }
            }
            if (command.equals("createMilestone")) {
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
                                resultNode.put("command", command);
                                resultNode.put("username", username);
                                resultNode.put("timestamp", timestamp);
                                resultNode.put("error", "Tickets " + idNode.asInt() + " already"
                                        + " assigned" + " to milestone " + unMil.getName() + ".");
                                outputs.add(resultNode);
                            }
                        }
                        ticketIds.add(idNode.asInt());
                        // setez pentru fiecare ticket din ce milestone face parte
                        allTickets.get(idNode.asInt()).setMilName(name);
                        actualHist(allTickets.get(idNode.asInt()), username, timestamp, "createMilestone", "");
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
                    resultNode.put("command", command);
                    resultNode.put("username", username);
                    resultNode.put("timestamp", timestamp);
                    resultNode.put("error", "The user does not have permission to "
                            + "execute this command: required role MANAGER; user role " + altTip.getRole() + ".");
                    outputs.add(resultNode);
                } else {

                    // caut in lista lui de subordonati daca exista acel milestone
                    for (String devUsername : assignedDevs) {
                        if (!managMeu.getSubordinates().contains(devUsername)) {
                            resultNode.put("error", "Developer " + devUsername +
                                    " is not part of manager's team.");
                            outputs.add(resultNode);
                            break;
                        }
                    }
                    // daca am trecut de toate astea sunt bine si creez milestone
                    if (validIdTicket) {
                        Milestone milestone = new Milestone(name, LocalDate.parse(dueDate), blockingFor, assignedDevs,
                                ticketIds, timestamp, username);

                        // nu stiu daca imi trebuie
                        // de fapt aici trebuie ca sa fiu sigura ca totul e ok
                        // acum am toti developeri valizi
                        // marchez si in lista lor separata
                        for (String devUsername : assignedDevs) {
                            for (User u : allUsers) {
                                if (u.getUsername().equals(devUsername)) {
                                    // am un developer
                                    Developer dev = (Developer) u;
                                    dev.addMilName(name);
                                }
                            }
                        }

                        // il pun cu toate milestoneurile
                        allMilestones.put(name, milestone);
                        // nu afisez nimic la succes

                        // trebuie asta ca sa pot face mereu aceste verificari
                        milestone.updateBlockState(new ArrayList<>(allMilestones.values()));
                    }
                }
            }

            if (command.equals("viewMilestones")) {
                resultNode.put("command", command);
                resultNode.put("username", username);
                resultNode.put("timestamp", timestamp);
                resultNode.put("milestones", viewGoodMilestones(username, allUsers,
                        new ArrayList<>(allMilestones.values()), timestamp));
                outputs.add(resultNode);
            }
            if (command.equals("assignTicket")) {
                // atribui unui developer un anumit tip de ticket
                Integer ticketId = cmd.get("ticketID").asInt();
                Ticket unTick = allTickets.get(ticketId);
                Developer devToAssig = (Developer) util;

                // acum avem ticketul si developerul
                // facem verificarile
                if (devToAssig.validAssigment(unTick, allMilestones).equals("valid")) {
                    // pot assigna fara probleme
                    devToAssig.addTickets(unTick);
                    // trebuie pus ca fiind assignat la data la care s a dat comanda
                    allTickets.get(ticketId).setAssignedAt(timestamp);
                    allTickets.get(ticketId).setStatus("IN_PROGRESS");
                    actualHist(unTick, username, timestamp, "assignTicket", "");
                    actualHist(allTickets.get(ticketId), username, timestamp, "changeStatus", "OPEN");

                } else {
                    resultNode.put("command", command);
                    resultNode.put("username", username);
                    resultNode.put("timestamp", timestamp);
                    resultNode.put("error",
                        devToAssig.validAssigment(unTick, allMilestones));
                    outputs.add(resultNode);
                }
            }
            if (command.equals("undoAssignTicket")) {
                // trebuie sters un anumit ticket
                Integer ticketId =  cmd.get("ticketID").asInt();
                Ticket unTick = allTickets.get(ticketId);
                // acum am ticketul, verific o singura chestie
                // daca este IN PROGRESS
                if (!unTick.getStatus().equals("IN_PROGRESS")) {
                    // nu e bun -> error
                    resultNode.put("command", command);
                    resultNode.put("username", username);
                    resultNode.put("timestamp", timestamp);
                    resultNode.put("error", "Only IN_PROGRESS tickets can be unassigned.");
                    outputs.add(resultNode);
                } else {
                    // e bun, trebuie sters
                    Developer devToUnAssig = (Developer) util;
                    devToUnAssig.unAssigTick(unTick);
                    // + schimbare status ticket in open
                    allTickets.get(ticketId).setStatus("OPEN");
                    allTickets.get(ticketId).setAssignedAt("");
                    // actual history
                    actualHist(unTick, username, timestamp, "undoAssignTicket", "");
                }
            }
            if (command.equals("viewAssignedTickets")) {
                // vad tickete pentru cine a cerut
                Developer dev = (Developer) util;

                // afisare efectiva
                resultNode.put("command", command);
                resultNode.put("username", username);
                resultNode.put("timestamp", timestamp);
                resultNode.put("assignedTickets", getAssignedTickets(dev, dev.getAssignedTckets()));
                outputs.add(resultNode);
            }
            if (command.equals("addComment")) {
                // adaug un comentariu
                String comment = cmd.get("comment").asText();
                Integer ticketID = cmd.get("ticketID").asInt();

                if (validAddComm(util, allTickets.get(ticketID), comment).equals("valid")) {
                    // e ok fac adaugarea
                    addComment(util, allTickets.get(ticketID), comment, timestamp);
                } else if (!validAddComm(util, allTickets.get(ticketID), comment).equals("ignore")) {
                    // ii dau cu eroare
                    resultNode.put("command", command);
                    resultNode.put("username", username);
                    resultNode.put("timestamp", timestamp);
                    resultNode.put("error", validAddComm(util, allTickets.get(ticketID), comment));
                    outputs.add(resultNode);
                }
            }
            if (command.equals("undoAddComment")) {
                Integer ticketId =  cmd.get("ticketID").asInt();
                // sterg ultimul comm al acestui ticket, daca se poate
                if (!undoAddComment(util, allTickets.get(ticketId)).equals("valid")
                        && !undoAddComment(util, allTickets.get(ticketId)).equals("ignore")) {
                    // amm eroare
                    resultNode.put("command", command);
                    resultNode.put("username", username);
                    resultNode.put("timestamp", timestamp);
                    // asta e singura eroare
                    resultNode.put("error", "Comments are not allowed on anonymous tickets.");
                    outputs.add(resultNode);
                }
            }
            if (command.equals("changeStatus") || command.equals("undoChangeStatus")) {
                // modific statusul unui ticket
                // mai intai verific sa vad daca e CLOSED
                Integer ticketID = cmd.get("ticketID").asInt();
                String result = null;
                String oldStatus = allTickets.get(ticketID).getStatus();
                Developer dev = (Developer) util;
                if (!dev.getAssignedTckets().contains(allTickets.get(ticketID))) {
                    result = "naspa";
                } else {
                    // vad in ce caz sunt
                    if (!allTickets.get(ticketID).getStatus()
                            .equals("CLOSED") && command.equals("changeStatus")) {
                        // modific status ticket cu +1
                        result = changingStatus((Developer) util, allMilestones.get(allTickets
                                .get(ticketID).getMilName()), allTickets.get(ticketID), 1);
                        // adaug in istoric
                        actualHist(allTickets.get(ticketID), username, timestamp, "changeStatus", oldStatus);
                    }
                    // caz particular aici am eroare cand este ticketul IN_PROCESS
                    if (!allTickets.get(ticketID).getStatus().equals("IN_PROGRESS")
                            && command.equals("undoChangeStatus")) {
                        // ma duc inapoi cu 1 status ticket
                        System.out.println(allTickets.get(ticketID).getStatus());
                        result = changingStatus((Developer) util, allMilestones.get(allTickets
                                .get(ticketID).getMilName()), allTickets.get(ticketID), 2);
                        actualHist(allTickets.get(ticketID), username, timestamp, "undoChangeStatus", oldStatus);
                    }
                }
                if (result != null && result.equals("naspa")) {
                    // eroare
                    System.out.println("intru pe aici??");
                    resultNode.put("command", command);
                    resultNode.put("username", username);
                    resultNode.put("timestamp", timestamp);
                    resultNode.put("error", "Ticket " + ticketID + " is not "
                         + "assigned to developer " + username + ".");
                    outputs.add(resultNode);
                }
            }
            if (command.equals("viewTicketHistory")) {
                // aici trebuie sa regandesc oleaca cam tot
                // daca ii da deassign trebuie retinut in alta parte ca a avut acel
                // ticket ca sa i pot vizualiza istoricul
                resultNode.put("command", command);
                resultNode.put("username", username);
                resultNode.put("timestamp", timestamp);
                resultNode.put("ticketHistory",
                    getHistoryTicket(util, new ArrayList<>(allTickets.values())));
                outputs.add(resultNode);
            }
        }
    }
}
