package forNotifications;

public interface Observer {
    // asta e nucleu la toata gestionarea de notif
    // asta spune cand trebuie sa adaugam o notif

    /**
     * metoda care declanseaza efectiv tot
     * sistemul de notificari
     */
    void update(String message);
}

