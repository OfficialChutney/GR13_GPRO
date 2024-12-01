package domainmodel;

import animal.Animal;
import animal.Bear;
import animal.Rabbit;
import animal.WolfPack;
import foliage.Grass;
import hole.Hole;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import hole.HoleType;

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
    private Helper helper;

    public Plane() {
        displaySize = 800;
        delay = 500;
        simulationStepLength = 200;
        rd = new Random();
        helper = new Helper();
    }

    public TestPackage startSimulation(int worldSize, HashMap<String, String> initialConditions, boolean isTest) {
        this.worldSize = worldSize;
        program = new Program(worldSize, displaySize, delay);
        world = program.getWorld();
        helper.setDisplayInfo(program);

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
                    case "bear" -> {
                        createObjectOnTile(Bear.class, value);
                    }
                    default -> {
                        System.out.println("could not determine type " + key);
                    }
                }
            }


        }


        long startingms = System.currentTimeMillis();

        if (!isTest) {

            program.show();


            for (int i = 1; i <= simulationStepLength; i++) {
                program.simulate();
                Map<Object, Location> entities = world.getEntities();

                for (Object entity : entities.keySet()) {
                    if (entity instanceof Animal a) {
                        a.setSteps(i);
                    }

                }
            }
        }

        stopSimulation();
        long stopms = System.currentTimeMillis();
        System.out.println("Time for simluation: " + (stopms - startingms));

        TestPackage tp = new TestPackage(world, program, world.getEntities());
        return tp;


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
                            Grass grassToPlace = new Grass(world);
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
                        } else if (objectType == Bear.class) {
                            Bear bear = new Bear(world);
                            world.setTile(locationOfObject, bear);
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

    public Program getProgram() {
        return program;
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
