import itumulator.simulator.Actor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class UserInterface {


    File inputFileDirectory;
    public UserInterface() {
        inputFileDirectory = new File("InputFiles");
    }

    public void startProgram() {

        for (File file : inputFileDirectory.listFiles()) {
            try {
                int worldSize;
                HashMap<String, String> initialConditions = new HashMap<>();


                FileReader fr = new FileReader(file.toString());

                BufferedReader br = new BufferedReader(fr);
                List<String> lines = new LinkedList<>(br.lines().toList());

                worldSize = Integer.parseInt(lines.getFirst());


                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i);

                    if(!line.isBlank()) {
                        String[] splitted = line.split(" ");
                        initialConditions.put(splitted[0],splitted[1]);
                    }

                }

                System.out.println("World Size: " + worldSize + " Initial conditions: "+initialConditions.toString());

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }




    }



}
