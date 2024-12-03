package domainmodel;

import animal.*;
import foliage.Grass;
import hole.Hole;
import hole.WolfHole;
import hole.RabbitHole;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        simulationStepLength = 200;
        rd = new Random();
    }

    public TestPackage startSimulation(int worldSize, boolean isTest, LinkedList<InitialConditions> icList) {
        this.worldSize = worldSize;
        program = new Program(worldSize, displaySize, delay);
        world = program.getWorld();
        Helper.setDisplayInfo(program);
        Helper.setSimulator(program.getSimulator());


        for (InitialConditions ic : icList) {

            String valueAsText = ic.getNumberOfObjects();
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

                switch (ic.getObject()) {
                    case "rabbit" -> {
                        createObjectOnTile(Rabbit.class, value);
                    }
                    case "burrow" -> {
                        createObjectOnTile(RabbitHole.class, value);
                    }
                    case "grass" -> {
                        createObjectOnTile(Grass.class, value);
                    }
                    case "wolf" -> {
                        createObjectOnTile(WolfPack.class, value);
                    }
                    case "bear" -> {
                        createObjectOnTile(Bear.class, value, ic.getCoordinates());
                    }
                    default -> {
                        System.out.println("could not determine type " + ic.getObject());
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
                int numOfRabbitHoles = 0;
                for (Object entity : entities.keySet()) {
                    if(entity instanceof RabbitHole) {
                        numOfRabbitHoles++;
                    }

                }
                System.out.println("Number of rabbitHoles: "+numOfRabbitHoles);
            }
        }

        stopSimulation();
        long stopms = System.currentTimeMillis();
        System.out.println("Time for simluation: " + (stopms - startingms));

        TestPackage tp = new TestPackage(world, program, world.getEntities());
        return tp;


    }

    private void createObjectOnTile(Class<?> objectType, int numberOfUnits) {
        createObjectOnTile(objectType, numberOfUnits, null);
    }

    private void createObjectOnTile(Class<?> objectType, int numberOfUnits, Location locationOfBear) {

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
                            Hole holeToPlace = new RabbitHole(world, locationOfObject);
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

                            if(locationOfBear == null) {
                                world.setTile(locationOfObject, bear);
                                System.out.println("I have placed random");
                            } else {
                                world.setTile(locationOfBear, bear);
                                System.out.println("I have been placed on: ("+locationOfBear.getX() + ","+locationOfBear.getY()+")");
                            }


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
