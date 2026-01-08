package fornotifications;

public interface Subject {
    // interfata care ma ajuta sa gestionez
    // develeoperii (ei sunt subject in cazul notif)

    /**
     * @param o
     * functie pentru adaugare
     */
    void addObserver(Observer o);

    /**
     *
     * @param o
     * functie pt stergere
     */
    void removeObserver(Observer o);

    /**
     *  efectiv metoda de notificare
     */
    void notifyObservers(String message);
}
