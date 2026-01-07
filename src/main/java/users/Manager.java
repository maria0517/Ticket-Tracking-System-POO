package users;

import java.util.List;

public class Manager extends User {
    // atribute specifice
    private String hireDate;
    private List<String> subordinates;

    // constructor
    public Manager(String username, String email,
            String hireDate, List<String> subordinates) {
        super(username, email, "MANAGER");
        this.hireDate = hireDate;
        this.subordinates = subordinates;
    }

    public List<String> getSubordinates() {
        return  subordinates;
    }
}
