package users;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;

public final class UserFactory {

    // nu pot lasa constructorul default
    private UserFactory() {
        throw new UnsupportedOperationException("Clasa utilitara nu trebuie instantiata!!!");
    }

    /**
     * creaza un obiect de tip User dintr-un Json
     */
    public static User createUser(final JsonNode node) {

        String role = node.get("role").asText();
        String username = node.get("username").asText();
        String email = node.get("email").asText();

        if (role.equals("REPORTER")) {
            return new Reporter(username, email);
        }
        if (role.equals("DEVELOPER")) {
            return new Developer(username, email, node.get("hireDate").asText(),
                    node.get("expertiseArea").asText(), node.get("seniority").asText());
        }
        if (role.equals("MANAGER")) {
            List<String> subs = new ArrayList<>();
            for (JsonNode s : node.get("subordinates")) {
                subs.add(s.asText());
            }
            return new Manager(username, email, node.get("hireDate").asText(), subs);
        }
        return null;
    }
}
