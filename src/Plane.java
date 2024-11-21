import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class Plane {

    private Program program;
    private World world;
    private int displaySize;
    private int delay;
    private Random rd;
    private int worldSize;


    public Plane() {
        displaySize = 800;
        delay = 300;
        rd = new Random();
    }

    public void start(int worldSize, HashMap<String, String> initialConditions) {
        this.worldSize = worldSize;
        program = new Program(worldSize, displaySize, delay);
        world = program.getWorld();
        setDisplayInfo();

        for (String key : initialConditions.keySet()) {

            String valueAsText = initialConditions.get(key);
            int value = -1;

            if (valueAsText.contains("-")) {
                String[] valueSplitted = valueAsText.split("-");
                int min = Integer.parseInt(valueSplitted[0]);
                int max = Integer.parseInt(valueSplitted[1]) + 1;
                value = rd.nextInt(min, max);
            } else {
                value = Integer.parseInt(valueAsText);
            }

            if (value != -1 ) {

                switch (key) {
                    case "rabbit" -> {
                        createRabbit(value);
                    }
                    case "burrow" -> {

                    }
                    case "grass" -> {
                        createGrass(value);
                    }
                    default -> {
                        System.out.println("could not determine type " + key);
                    }
                }
            } else {
                System.out.println("Mom i shitted.");
            }


        }


        program.show();

        for (int i = 0; i < 20; i++) {
            program.simulate();
        }

        stopProgram();

    }

    private void createGrass(int numberOfGrass) {

        for (int i = 0; i < numberOfGrass; i++) {
            boolean tileIsEmpty = false;
            while (!tileIsEmpty) {
                int x = rd.nextInt(worldSize);
                int y = rd.nextInt(worldSize);
                Location locationOfGrass = new Location(x, y);
                if (world.getTile(locationOfGrass) == null) {
                    tileIsEmpty = true;
                    Grass grassToPlace = new Grass(world, locationOfGrass);
                    world.setTile(locationOfGrass, grassToPlace);
                }


            }
        }

    }

    private void createRabbit(int numberOfRabbits) {

    }

    private void createBurrow(int numberOfRabbits) {

    }

    private void stopProgram() {
        try {
            Thread.sleep(2000);

        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        program.getFrame().setVisible(false);
    }

    private void setDisplayInfo() {
        DisplayInformation grassDisplay = new DisplayInformation(Color.black, "grass");
        program.setDisplayInformation(Grass.class, grassDisplay);
    }


}
