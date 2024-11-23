import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class UserInterface {


    private File inputFileDirectory;

    public UserInterface() {
        inputFileDirectory = new File("InputFiles");
    }

    public void startProgram() {
        int start = 0;
        for (File file : Objects.requireNonNull(inputFileDirectory.listFiles())) {
            if (start >= 5) {


                try {
                    int worldSize;
                    HashMap<String, String> initialConditions = new HashMap<>();


                    FileReader fr = new FileReader(file.toString());

                    BufferedReader br = new BufferedReader(fr);
                    List<String> lines = new LinkedList<>(br.lines().toList());

                    worldSize = Integer.parseInt(lines.getFirst());


                    for (int i = 1; i < lines.size(); i++) {
                        String line = lines.get(i);

                        if (!line.isBlank()) {
                            String[] splitted = line.split(" ");
                            initialConditions.put(splitted[0].toLowerCase(), splitted[1]);
                        }

                    }


                    System.out.println(initialConditions.toString());
                    Plane plane = new Plane();
                    plane.startSimulation(worldSize, initialConditions);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

            }
            start++;
        }

        System.exit(0);


    }


}
