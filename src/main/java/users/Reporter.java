package users;

public class Reporter extends User {
    // un tip de user
    public Reporter(final String username, final String email) {
        super(username, email, "REPORTER");
    }
}
