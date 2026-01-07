package users;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;

public class UserFactory {

    public static User createUser(JsonNode node) {

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
            return new Manager( username, email, node.get("hireDate").asText(), subs);
        }
        return null;
    }
}
