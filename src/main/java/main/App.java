package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import users.User;
import users.UserFactory;

/**
 * main.App represents the main application logic that processes input commands,
 * generates outputs, and writes them to a file
 */
public class App {
    private static final String inputUserFile = "input/database/users.json";

    private static final ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();

    /**
     * Runs the application: reads commands from an input file,
     * processes them, generates results, and writes them to an output file
     *
     * @param inputPath path to the input file containing commands
     * @param outputPath path to the file where results should be written
     */
    public static void run(String inputPath, String outputPath) {
        // feel free to change this if needed (however keep 'outputs' variable name to be used for writing)
        List<ObjectNode> outputs = new ArrayList<>();
		// creare mapper ca sa pot citi din fisiere
		ObjectMapper mapper = new ObjectMapper();
		// lista useri -> urmeaza useri de facut
		List<User> allUsers = new ArrayList<>();

        /*
            TODO 1 :
            Load initial user data and commands. we strongly recommend using jackson library.
            you can use the reading from hw1 as a reference.
            however you can use some of the more advanced features of
            jackson library, available here: https://www.baeldung.com/jackson-annotations
        */

		// citire useri prima data
		// fac cu try-catch pentru tratare simulatana a exceptiilor

		try {
			File userFile = new File(inputUserFile);
			JsonNode usersFromFile = mapper.readTree(userFile);
			// ii adaug pe cate unu
			for (JsonNode u : usersFromFile) {
				allUsers.add(UserFactory.createUser(u));
			}
		} catch (Exception e) {
			System.out.println("mica eroare la useri");
		}

		// acum comenzile
		// lista de comenzi -> le prelucrez in alta clasa (Commands)
		// ca sa am mainul cat mai curat
		List<JsonNode> listComm = new ArrayList<>();
		try {
			File commFile = new File(inputPath);
			JsonNode allComm = mapper.readTree(commFile);
			if (allComm != null && allComm.isArray()) {
				for (JsonNode node : allComm) {
					listComm.add(node);
				}
			}
		} catch (IOException e) {
			System.out.println("mica eroare la comenzi");
		}

        // TODO 2: process commands.
		Commands commandsProcessor = new Commands(listComm);
		// ii dau cu tot cu outputs, ca sa nu mai creez alt obiect
		// + useri care sunt cititi deja
		commandsProcessor.prelucComm(outputs, allUsers);

        // TODO 3: create objectnodes for output, add them to outputs list.
		// fac in clasa Commands, in metoda prelucComm

        // DO NOT CHANGE THIS SECTION IN ANY WAY
        try {
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            writer.withDefaultPrettyPrinter().writeValue(outputFile, outputs);
        } catch (IOException e) {
            System.out.println("error writing to output file: " + e.getMessage());
        }
    }
}
