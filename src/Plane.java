import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
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
                System.out.println("I am random " + value);

            } else {
                value = Integer.parseInt(valueAsText);
            }

            if (value != -1 ) {

                switch (key) {
                    case "rabbit" -> {
                        //TODO MAKE PHILIP CREATE THE FUCKING RABBIT
                    }
                    case "burrow" -> {
                        createObjectOnTile(RabbitHole.class, value);
                    }
                    case "grass" -> {
                        createObjectOnTile(Grass.class,value);
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
        createObjectOnTile(Grass.class, numberOfGrass);
    }

    private void createRabbit(int numberOfRabbits) {

    }
    private void createBurrow(int numberOfRabbits) {

    }

    private void createObjectOnTile(Class<?> objectType, int numberOfUnits) {
        for (int i = 0; i < numberOfUnits; i++) {
            boolean tileIsEmpty = false;
            while (!tileIsEmpty) {
                int x = rd.nextInt(worldSize);
                int y = rd.nextInt(worldSize);
                Location locationOfObject = new Location(x, y);

                if(NonBlocking.class.isAssignableFrom(objectType)) {

                    if (!world.containsNonBlocking(locationOfObject)) {
                        tileIsEmpty = true;

                        if(objectType == Grass.class) {
                            Grass grassToPlace = new Grass(world, locationOfObject);
                            world.setTile(locationOfObject, grassToPlace);
                        } else if(objectType == RabbitHole.class) {
                            RabbitHole holeToPlace = new RabbitHole(world, locationOfObject);
                            world.setTile(locationOfObject, holeToPlace);
                        }
                    }

                } else {

                    Object objectOnTile = world.getTile(locationOfObject);

                    if(objectOnTile == null || objectOnTile instanceof NonBlocking) {
                        //TODO Put rabbit creation here.
                    }

                }







            }
        }
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

        //Set display for Grass
        DisplayInformation grassDisplay = new DisplayInformation(Color.black, "grass");
        program.setDisplayInformation(Grass.class, grassDisplay);

        //Set display for Rabbitholes
        DisplayInformation rabbitHoleDisplay = new DisplayInformation(Color.orange, "hole-small");
        program.setDisplayInformation(RabbitHole.class, rabbitHoleDisplay);
    }


}
