package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import milestones.Milestone;
import milestones.MilestoneGen;
import searchfilters.*;
import tickets.*;
import users.Developer;
import users.Manager;
import users.User;

import java.time.LocalDate;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static main.ViewTickets.getAssignedTickets;
import static main.ViewTickets.getHistoryTicket;
import static main.ViewTickets.getVisibleTickets;
import static main.ViewNotifications.getNotifications;
import static metrics.PerformanceReport.generatePerformanceReport;
import static metrics.ReportOutputGenerator.reportFormat;
import static metrics.ResolutionReportOutputGenerator.resolutionReportFormat;
import static tickets.Comment.validAddComm;
import static tickets.Comment.addComment;
import static tickets.Comment.undoAddComment;
import static milestones.Milestone.updateMilestones;
import static milestones.ViewMilestones.viewGoodMilestones;
import static tickets.HistoryOfTicket.actualHist;
import static tickets.Status.changingStatus;


public class Commands {

    public static final int TESTING_PER = 12;

    // lista pentru comm si mapper pentru formatare output
    private final List<JsonNode> commands;
    private final ObjectMapper mapper = new ObjectMapper();

    // constructorul
    public Commands(final List<JsonNode> commands) {
        this.commands = commands;
    }

    // lista de tichete
    private final Map<Integer, Ticket> allTickets = new HashMap<>();

    private final Map<String, Milestone> allMilestones = new HashMap<>();

    // data pentru a putea vedea perioada de testing
    private boolean firstComm = false;
    private String testPeriod = null;
    private String testPerEnd = null;
    // 12 zile pentru testare

    /**
     *  // efectiv parcurgere comenzi si procesarea lor
     */
    public void prelucComm(final List<ObjectNode> outputs, final List<User> allUsers) {
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
            // toate mesajele de output incep cu asta
            resultNode.put("command", command);
            resultNode.put("username", username);
            resultNode.put("timestamp", timestamp);

            if (!firstComm) {
                firstComm = true;
                // acum trebuie sa iau timpul de start
                testPeriod = timestamp;
                // am timpul de start; trebuie cel de final
                LocalDate start = LocalDate.parse(testPeriod);
                testPerEnd = start.plusDays(TESTING_PER).toString();
            }

            updateMilestones(allMilestones, timestamp, allTickets);

            if (command.equals("startTestingPhase")) {
                // de la asta imi crapa 18
                // presupun ca este o comanda valida, care trebuie executata
                // incep o noua perioada in care se pot raporta tichete
                // resetez ce aveam initial
                testPeriod = timestamp;
                LocalDate start = LocalDate.parse(testPeriod);
                testPerEnd = start.plusDays(TESTING_PER).toString();
            }
            // creare output; daca am
            if (command.equals("viewTickets")) {
                resultNode.put("tickets", getVisibleTickets(util,
                        new ArrayList<>(allTickets.values())));
                outputs.add(resultNode);
            }

            // trebuie sa fac logica de validare tickete in functie de perioada testare
            // aici incep sa vad ce tip de comanda am
            if (command.equals("reportTicket")) {
                // am mutat mai jos toata jucaria
                createTick(username, timestamp, params, allUsers, resultNode, outputs, testPerEnd);
            }
            if (command.equals("createMilestone")) {
                // apelez direct functie din alta parte ca sa nu am aglomerat aici
                MilestoneGen.createMilestone(cmd, allMilestones, allTickets,
                        allUsers, resultNode, outputs, timestamp, username, util);
            }

            if (command.equals("viewMilestones")) {
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
                    allTickets.get(ticketId).setAssignedtTo(devToAssig.getUsername());
                    allTickets.get(ticketId).setStatus("IN_PROGRESS");
                    actualHist(unTick, username, timestamp, "assignTicket", "");
                    actualHist(allTickets.get(ticketId), username,
                            timestamp, "changeStatus", "OPEN");

                } else {
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
                // afisare efectiva
                // nu am tratat cazul in care e manager!!!!!
                // de acolo crapa
                if (util.getRole().equals("DEVELOPER")) {
                    resultNode.put("assignedTickets",
                            getAssignedTickets(((Developer) util).getAssignedTckets()));
                }
                outputs.add(resultNode);
            }
            if (command.equals("addComment")) {
                // adaug un comentariu
                String comment = cmd.get("comment").asText();
                Integer ticketID = cmd.get("ticketID").asInt();

                if (validAddComm(util, allTickets.get(ticketID), comment).equals("valid")) {
                    // e ok fac adaugarea
                    addComment(util, allTickets.get(ticketID), comment, timestamp);
                } else if (!validAddComm(util, allTickets.get(ticketID),
                        comment).equals("ignore")) {
                    // ii dau cu eroare
                    resultNode.put("error", validAddComm(util, allTickets.get(ticketID), comment));
                    outputs.add(resultNode);
                }
            }
            if (command.equals("undoAddComment")) {
                Integer ticketId =  cmd.get("ticketID").asInt();
                // sterg ultimul comm al acestui ticket, daca se poate
                if (!undoAddComment(util, allTickets.get(ticketId)).equals("valid")
                        && !undoAddComment(util, allTickets.get(ticketId)).equals("ignore")) {
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
                        result = changingStatus((Developer) util, timestamp,
                                allMilestones, allTickets.get(ticketID), 1);
                        // adaug in istoric
                        actualHist(allTickets.get(ticketID), username,
                                timestamp, "changeStatus", oldStatus);
                    }
                    // caz particular aici am eroare cand este ticketul IN_PROCESS
                    if (!allTickets.get(ticketID).getStatus().equals("IN_PROGRESS")
                            && command.equals("undoChangeStatus")) {
                        // ma duc inapoi cu 1 status ticket
                        result = changingStatus((Developer) util, timestamp,
                                allMilestones, allTickets.get(ticketID), 2);
                        actualHist(allTickets.get(ticketID), username,
                                timestamp, "undoChangeStatus", oldStatus);
                    }
                }
                if (result != null && result.equals("naspa")) {
                    // eroare
                    resultNode.put("error", "Ticket " + ticketID + " is not "
                            + "assigned to developer " + username + ".");
                    outputs.add(resultNode);
                }
            }
            if (command.equals("viewTicketHistory")) {
                // aici trebuie sa regandesc oleaca cam tot
                // daca ii da deassign trebuie retinut in alta parte ca a avut acel
                // ticket ca sa i pot vizualiza istoricul
                resultNode.put("ticketHistory",
                        getHistoryTicket(util,
                                new ArrayList<>(allTickets.values()), allMilestones));
                outputs.add(resultNode);
            }
            if (command.equals("search")) {
                // toate filtrele cu care lucrez mai departe
                // le am mutat in alta parte
                outputs.add(SearchSelector.performSearch(cmd, util,
                        allUsers, allTickets, allMilestones));
            }
            if (command.equals("viewNotifications")) {
                // doar pentru developer
                Developer dev =  (Developer) util;
                resultNode.put("notifications", getNotifications(dev));
                outputs.add(resultNode);
            }
            if (command.equals("generateCustomerImpactReport")
                    || command.equals("generateTicketRiskReport")) {
                // trebuie mai intai sa mi fac lista cu toate
                // ticketele pe care le folosesc in raport
                List<Ticket> ticksForReport = GeneratorTicketForRep.selectTickets(command,
                        allTickets, timestamp);
                // am ticketele apelez direct
                // fac in functia de format distinctia intre cele doua
                // tipuri de rapoarte
                resultNode.put("report", reportFormat(ticksForReport, command));
                outputs.add(resultNode);
            }
            if (command.equals("generateResolutionEfficiencyReport")) {
                List<Ticket> ticksForReport = GeneratorTicketForRep.selectTickets(command,
                        allTickets, timestamp);
                resultNode.put("report", resolutionReportFormat(ticksForReport));
                outputs.add(resultNode);
            }
            if (command.equals("appStabilityReport")) {
                // aici e oleaca de scris
                List<Ticket> ticksForReport = GeneratorTicketForRep.selectTickets(command,
                        allTickets, timestamp);
                resultNode.put("report", reportFormat(ticksForReport, command));
                outputs.add(resultNode);
            }
            if (command.equals("generatePerformanceReport")) {
                List<Ticket> ticksForReport = GeneratorTicketForRep.selectTickets(command,
                        allTickets, timestamp);
                // apelez direct functia de afisare
                // generatePerformanceReport
                resultNode.put("report",
                        generatePerformanceReport(ticksForReport, allUsers, (Manager) util));
                // ipotetic e bine
                outputs.add(resultNode);
            }
        }
    }

    public void createTick(final String username, final String timestamp,
         final JsonNode params, final List<User> allUsers, final ObjectNode resultNode,
         final List<ObjectNode> outputs,  final String testPerEnd) {
        // aici verific daca nu cumva ticketul nu mai este in perioada de testare
        LocalDate ticketDate = LocalDate.parse(timestamp);
        LocalDate end = LocalDate.parse(testPerEnd);

        // verific daca nu am trecut
        if (ticketDate.isAfter(end)) {
            resultNode.put("error", "Tickets can only"
                    + " be reported during testing phases.");
            outputs.add(resultNode);
            return; // nu mai creez ticketul
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
            resultNode.put("error", "The user " + username + " does not exist.");
            outputs.add(resultNode);
            return;
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
                    newTicket = new FeatureRequestTicket.Builder().setId(newId).
                            setTitle(title).setBusinessPriority(businessPriority).
                            setExpertiseArea(expertiseArea).setDescription(description)
                            .setReportedBy(reportedBy).setBusinessValue(businessValue)
                            .setCustomerDemand(customerDemand).setCreatedAt(timestamp).build();
                } else {
                    // nu e raportat de nimeni; nu se poate
                    resultNode.put("error", "Anonymous reports are "
                            + "only allowed for tickets of type BUG.");
                    outputs.add(resultNode);
                    return;
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
                int usabilityScore =   Integer.parseInt(params.get(
                        "usabilityScore").asText());
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
                            .setSuggestedFix(suggestedFix).setCreatedAt(timestamp).build();
                } else {
                    // nu e raportat de nimeni; nu se poate
                    resultNode.put("error", "Anonymous reports are "
                            + "only allowed for tickets of type BUG.");
                    outputs.add(resultNode);
                    return;
                }
            }
            // adaug daca exista ceva
            if (newTicket != null) {
                allTickets.put(newId, newTicket);
            }
        }
    }
}
