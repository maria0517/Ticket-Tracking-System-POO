package forNotifications;

public interface Observer {
    // asta e nucleu la toata gestionarea de notif
    // asta spune cand trebuie sa adaugam o notif
    void update(String message);
}

