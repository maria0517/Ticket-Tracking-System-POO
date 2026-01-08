package users;

import java.util.List;

public final class Manager extends User {
    // atribute specifice
    private String hireDate;
    private List<String> subordinates;

    // constructor
    public Manager(final String username, final String email,
            final String hireDate, final List<String> subordinates) {
        super(username, email, "MANAGER");
        this.hireDate = hireDate;
        this.subordinates = subordinates;
    }

    public List<String> getSubordinates() {
        return  subordinates;
    }
}
