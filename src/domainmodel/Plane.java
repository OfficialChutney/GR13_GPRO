package domainmodel;

import animal.*;
import foliage.BerryBush;
import foliage.Grass;
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

    public Plane() {
        displaySize = 800;
        delay = 500;
        simulationStepLength = 200;
        rd = new Random();
    }

    public TestPackage startSimulation(int worldSize, boolean isTest, LinkedList<InitialConditions> icList) {
        this.worldSize = worldSize;
        initializeProgram();

        initializeEntities(icList);

        long startingms = System.currentTimeMillis();

        if (!isTest) {
            runSimulation();
        }

        stopSimulation();
        long stopms = System.currentTimeMillis();
        System.out.println("Time for simulation: " + (stopms - startingms));

        return createTestPackage();
    }

    private void initializeProgram() {
        program = new Program(worldSize, displaySize, delay);
        world = program.getWorld();
        Helper.setDisplayInfo(program);
        Helper.setSimulator(program.getSimulator());
    }

    private void initializeEntities(LinkedList<InitialConditions> icList) {
        for (InitialConditions ic : icList) {

            initializeEntity(ic);
        }
    }

    private int parseValue(String valueAsText) {
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
        return value;
    }

    private void initializeEntity(InitialConditions ic) {

        String objectType = ic.getObject();

        switch (objectType) {
            case "rabbit" -> createObjectOnTile(Rabbit.class, ic);
            case "burrow" -> createObjectOnTile(RabbitHole.class, ic);
            case "grass" -> createObjectOnTile(Grass.class, ic);
            case "wolf" -> createObjectOnTile(WolfPack.class, ic);
            case "bear" -> createObjectOnTile(Bear.class, ic);
            case "carcass" -> createObjectOnTile(Cadavar.class,ic);
            default -> System.out.println("could not determine type " + objectType);
        }
    }

    private void runSimulation() {
        program.show();
        for (int i = 1; i <= simulationStepLength; i++) {
            program.simulate();
        }
    }

    private int countEntitiesOfType(Class<?> entityType) {
        Map<Object, Location> entities = world.getEntities();
        int count = 0;
        for (Object entity : entities.keySet()) {
            if (entityType.isInstance(entity)) {
                count++;
            }
        }
        return count;
    }

    private TestPackage createTestPackage() {
        return new TestPackage(world, program, world.getEntities());
    }


    private void createObjectOnTile(Class<?> objectType, InitialConditions ic) {
        int numberOfUnits = parseValue(ic.getNumberOfObjects());

        Location specificLocation = ic.getCoordinates();



        for (int i = 0; i < numberOfUnits; i++) {
            boolean tileIsEmpty = false;
            while (!tileIsEmpty) {

                Location locationOfObject;
                if (specificLocation == null) {
                    locationOfObject = getRandomLocation();
                } else {
                    locationOfObject = specificLocation;

                    if (NonBlocking.class.isAssignableFrom(objectType)) {

                        while (world.getNonBlocking(locationOfObject) != null) {
                            Location newLocation = null;
                            while (newLocation == null) {
                                newLocation = getRandomLocation();
                                if (world.getNonBlocking(newLocation) != null) {
                                    newLocation = null;
                                } else {
                                    Object nonBlockingObject = world.getNonBlocking(newLocation);
                                    world.move(nonBlockingObject, newLocation);
                                }
                            }
                        }


                    } else {
                        while (world.getTile(locationOfObject) instanceof Animal a) {
                            Location newLocation = null;
                            while (newLocation == null) {
                                newLocation = getRandomLocation();
                                if (world.getTile(newLocation) instanceof Animal) {
                                    newLocation = null;
                                } else {
                                    world.move(a, newLocation);
                                }
                            }
                        }


                    }

                }

                if (NonBlocking.class.isAssignableFrom(objectType)) {

                    if (!world.containsNonBlocking(locationOfObject)) {
                        tileIsEmpty = true;

                        if (objectType == Grass.class) {
                            Grass grassToPlace = new Grass(world);
                            world.setTile(locationOfObject, grassToPlace);
                        } else if (objectType == RabbitHole.class) {
                            RabbitHole holeToPlace = new RabbitHole(world, locationOfObject);
                            world.setTile(locationOfObject, holeToPlace);
                        } else if (objectType == BerryBush.class) {
                            BerryBush bush = new BerryBush();
                            world.setTile(locationOfObject, bush);
                        } else if (objectType == Cadavar.class) {
                            Cadavar carcass;
                            if(ic.isFungi()) {
                                carcass = new Cadavar(world, true, 60,80);
                            } else {
                                carcass = new Cadavar(world, false, 60,80);
                            }

                            world.setTile(locationOfObject, carcass);


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

                            InitialConditions newIc = new InitialConditions("berrybush", "1-5", null);

                            createObjectOnTile(BerryBush.class, newIc);

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

    public Location getRandomLocation() {
        int x = rd.nextInt(worldSize);
        int y = rd.nextInt(worldSize);
        return new Location(x, y);
    }


}
