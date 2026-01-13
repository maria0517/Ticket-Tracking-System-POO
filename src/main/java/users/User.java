package users;

public abstract class User {
    // factory
    protected String username;
    protected String email;
    protected String role;

    public User(final String username, final String mail, final String role) {
         this.username = username;
         this.email = mail;
        this.role = role;
    }

    /**
     * Return username
     */
    public String getUsername() {
            return username;
        }

    /**
     * Return role
     */
    public String getRole() {
            return role;
        }
}
