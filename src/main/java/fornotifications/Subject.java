package forNotifications;

public interface Subject {
    // interfata care ma ajuta sa gestionez
    // develeoperii (ei sunt subject in cazul notif)
    void addObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers(String message);
}
