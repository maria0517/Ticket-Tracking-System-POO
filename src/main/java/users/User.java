package users;

public abstract class User {
    // factory
        protected String username;
        protected String email;
        protected String role;

        public User(String username, String mail, String role) {
            this.username = username;
            this.email = mail;
            this.role = role;
        }

        public String getUsername() {
            return username;
        }
        public String getMail() {
            return email;
        }
        public String getRole() {
            return role;
        }
}
