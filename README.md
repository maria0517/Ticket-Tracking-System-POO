# Tema 2 - Ticket Tracking System (Programare Orientată pe Obiecte)

## 1. Descriere Generală
Această temă implementează un sistem modular de ticket tracking și gestiune a unui proiect software. Aplicația gestionează o bază de utilizatori, procesează comenzi și tichete, monitorizează dependențele dintre milestone-uri, trimite notificări automate și generează rapoarte și metrici de performanță.

Pentru manipularea facilă a datelor am folosit noduri JSON. Spre deosebire de tema anterioară, am separat strict responsabilitățile: în `main` / `App.java` am păstrat doar citirea datelor și rutarea comenzilor prin `prelucComm`, procesarea efectivă a fiecărei comenzi fiind delegată în clase dedicate.

---

## 2. Structura Proiectului și Pachete

Am modularizat arhitectura în pachete specifice pentru decuplare și mentenanță:
* **`tickets`**: Modelează entitățile de tichete (clasa abstractă `Ticket` și subtipurile sale concrete).
* **`users`**: Conține ierarhia de utilizatori și mecanismele de instanțiere pe baza rolurilor.
* **`milestones`**: Gestionează stările, deadline-urile și dependențele de deblocare ale milestone-urilor.
* **`notifications`**: Sistemul de mesagerie și abonare pentru evenimentele din proiect.
* **`filters` / `search`**: Algoritmii și strategiile pentru filtrarea și căutarea tichetelor și dezvoltatorilor.
* **`metrics`**: Conține logica de generare a rapoartelor, fiind structurat în:
  * Metode pentru calculul efectiv al scorurilor și performanței.
  * Metode pentru formatarea și construirea output-urilor.
* **`constants`**: Pachet dedicat pentru izolarea constantelor și valorilor fixe (magic numbers).
* **`main`**: Conține punctul de intrare `App.java`, clasa `Milestone`, logica de rutare a comenzilor și funcțiile de afișare (`viewTickets`, `viewHistory` etc.).

---

## 3. Design Patterns Utilizate

* **Builder Pattern (`tickets`):**
  * Tichetele prezintă o structură complexă cu multiple atribute. Am definit o clasă abstractă `Ticket` extinsă de cele 3 tipuri concrete (de ex. `BugTicket`).
  * Fiecare tip de tichet definește propriul `Builder` intern. Constructorii claselor sunt privați, iar instanțierea se face controlat exclusiv prin apelul metodei `.build()`, asigurând consistența obiectelor și izolarea logicii de construcție.

* **Factory Method Pattern (`users`):**
  * Folosit pentru instanțierea tipurilor derivate din clasa părinte `User`.
  * Permite determinarea clasei concrete la runtime pe baza atributului `role` citit din JSON, garantând crearea unică și corectă a fiecărui tip de utilizator cu toate atributele sale.

* **Observer Pattern (`notifications`):**
  * Folosit pentru sistemul automat de notificări.
  * Obiectele de tip `Milestone` acționează ca subiecți (*Subjects*), iar `Developer`-ii funcționează ca observatori (*Observers*).
  * În momentul în care un milestone este deblocat, se apropie de data limită (*due date*) sau este depășit, observatorii abonați sunt notificați automat, decuplând complet logica de notificare de restul aplicației.

* **Strategy Pattern (`filters`):**
  * Utilizat pentru mecanismele flexibile de căutare în cadrul tichetelor sau dezvoltatorilor.
  * Permite combinarea dinamică a diferitelor criterii și filtre la execuție, fără a altera sau replica logica principală de căutare.

---

## 4. Logica de Business și Provocări Întâmpinate

* **Ciclul de viață al proiectului:** Perioadele proiectului (testing, debugging, reporting) sunt gestionate prin variabile de stare. Data de început a proiectului este setată la timestamp-ul primei comenzi primite, iar comanda `startTestingPhase` actualizează dinamic data de start a etapei curente.
* **`updateMilestones`:** Se ocupă de deblocarea milestone-urilor ale căror cerințe au fost îndeplinite și recalculează prioritățile tichetelor asociate.
* **`changingStatus`:** Tratează tranzițiile de status ale tichetelor și verifică în cascadă dacă modificarea influențează starea milestone-ului din care fac parte.
* **Rezolvare anomalie de prioritizare (Edge Case - Test 18):**
  * Am întâmpinat o problemă legată de momentul creșterii priorității unui tichet în cazul milestone-urilor blocate (de exemplu, un milestone creat pe 20.10, dar deblocat abia pe 26.10, sărea intervalul de incrementare de pe 29.10 și declanșa modificarea eronat mai târziu).
  * Am rezolvat această problemă prin adăugarea câmpului `activatedAt` în clasa `Milestone`, care reține timestamp-ul exact la care milestone-ul a devenit activ (folosit ulterior ca referință de bază pentru recalcularea priorității); dacă milestone-ul nu a fost blocat inițial, `activatedAt` coincide cu `createdAt`.
