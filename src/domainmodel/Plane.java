package domainmodel;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.*;

public class Plane {

    private Program program;
    private World world;
    private int displaySize;
    private int delay;
    private Random rd;
    private int worldSize;
    private int simulationStepLength;
    private static int numOfNonBlocking;

    public Plane() {
        displaySize = 800;
        delay = 500;
        simulationStepLength = 20;
        rd = new Random();
    }

    public void startSimulation(int worldSize, HashMap<String, String> initialConditions) {
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

            if (value != -1) {

                switch (key) {
                    case "rabbit" -> {
                        createObjectOnTile(Rabbit.class, value);
                    }
                    case "burrow" -> {
                        createObjectOnTile(Hole.class, value);
                    }
                    case "grass" -> {
                        createObjectOnTile(Grass.class, value);
                    }
                    case "wolf" -> {
                        createObjectOnTile(WolfPack.class, value);
                    }
                    default -> {
                        System.out.println("could not determine type " + key);
                    }
                }
            }


        }


        program.show();
        long startingms = System.currentTimeMillis();
        for (int i = 0; i < simulationStepLength; i++) {
            program.simulate();
            System.out.println("Step: " + world.getCurrentTime());


            //Denne kode henter alle kaniner og tester, om de stadig er p� spillefladen.
            //Det ses tydeligt, at i visse tilf�lde, slettes kaninerne fra spillefladen, n�r "remove" metoden benyttes, selvom de ikke burde.
            Set<Object> allEntities = world.getEntities().keySet();
            Set<Rabbit> allRabbits = new HashSet<>();
            for (Object o : allEntities) {
                if (o instanceof Rabbit r) {
                    allRabbits.add(r);
                }
            }

            System.out.println("Number Of rabbits in world: " + allRabbits.size());

        }

        stopSimulation();
        long stopms = System.currentTimeMillis();
        System.out.println("Time for simluation: " + (stopms - startingms));

    }

    private void createObjectOnTile(Class<?> objectType, int numberOfUnits) {

        for (int i = 0; i < numberOfUnits; i++) {
            boolean tileIsEmpty = false;
            while (!tileIsEmpty) {
                int x = rd.nextInt(worldSize);
                int y = rd.nextInt(worldSize);
                Location locationOfObject = new Location(x, y);

                if (NonBlocking.class.isAssignableFrom(objectType)) {

                    if (!world.containsNonBlocking(locationOfObject)) {
                        tileIsEmpty = true;

                        if (objectType == Grass.class) {
                            Grass grassToPlace = new Grass(world, locationOfObject);
                            world.setTile(locationOfObject, grassToPlace);
                        } else if (objectType == Hole.class) {
                            Hole holeToPlace = new Hole(world, locationOfObject, HoleType.RABBITHOLE);
                            world.setTile(locationOfObject, holeToPlace);
                        }
                    }

                } else {

                    Object objectOnTile = world.getTile(locationOfObject);

                    if (objectOnTile == null || objectOnTile instanceof NonBlocking) {
                        tileIsEmpty = true;
                        if (objectType == Rabbit.class) {
                            Rabbit rabbit = new Rabbit(world);
                            world.setTile(locationOfObject, rabbit);
                        } else if (objectType == WolfPack.class) {
                            new WolfPack(numberOfUnits, locationOfObject, world);
                            return;
                        }
                    }

                }

            }
        }
    }

    private void stopSimulation() {
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
        program.setDisplayInformation(Hole.class, rabbitHoleDisplay);

        //Set display for Rabbit
        DisplayInformation rabbitDisplay = new DisplayInformation(Color.orange, "rabbit-large");
        program.setDisplayInformation(Rabbit.class, rabbitDisplay);

        //Set display for Wolf
        DisplayInformation wolfDisplay = new DisplayInformation(Color.orange, "wolf");
        program.setDisplayInformation(Wolf.class, wolfDisplay);
    }

    public static void increaseNonBlocking() {
        numOfNonBlocking++;
    }

    public static void decreaseNonBlocking() {
        numOfNonBlocking--;
    }

    public static int getNonBlocking() {
        return numOfNonBlocking;
    }


}
