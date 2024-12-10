package domainmodel;

import animal.*;
import animal.carnivores.Bear;
import animal.carnivores.WolfPack;
import animal.herbivore.Rabbit;
import foliage.BerryBush;
import foliage.Grass;
import hole.RabbitHole;
import information_containers.InitialConditions;
import information_containers.TestPackage;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.*;

/**
 * Klassen ansvarlig for simulation af en verden.
 */
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
        delay = 300;
        simulationStepLength = 40;
        rd = new Random();
    }

    /**
     * Starter en simulation af en verden. Kalder først {@link #initializeProgram()} som initalisere programmet,
     * og derefter {@link #initializeEntities(LinkedList)} som initialisere objekterne ud fra startparametrene.
     * Derefter kaldes {@link #runSimulation()} som kører simulationen. Efter simulationen kaldes {@link #stopSimulation()}.
     * @param worldSize størrelsen på verdenen.
     * @param isTest hvorvidt denne simulation er en del af en UnitTest
     * @param icList listen af startparametre.
     * @return {@link TestPackage} som indeholder slutParametre for verden.
     */
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

    /**
     * Initialisere {@link Program} og fra program henter {@link World}.
     * Den sætter også {@link itumulator.executable.DisplayInformation} for alle de {@link itumulator.simulator.Actor} og {@link NonBlocking} objekter,
     * som ikke implementere {@link itumulator.executable.DynamicDisplayInformationProvider}. Dette gør den via klassen {@link Helper}.
     * Den sætter også den nuværende {@link itumulator.simulator.Simulator} inde i {@link Helper} klassen.
     */
    private void initializeProgram() {
        program = new Program(worldSize, displaySize, delay);
        world = program.getWorld();
        Helper.setDisplayInfo(program);
        Helper.setSimulator(program.getSimulator());
    }

    /**
     * Initialisere alle vores {@link itumulator.simulator.Actor} og {@link NonBlocking} elementer fra vores startparametre.
     * Sender hver individuelle objekt videre til {@link #initializeEntity(InitialConditions)}
     * @param icList en {@link LinkedList} af {@link InitialConditions}, som er alle startparametre for hvert objekt der skal placeres i verdenen.
     */
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

    /**
     * Tager det objekt der ønskes at initialiseres fra {@link InitialConditions}, og sender det specifikke objekt videre til
     * initialisering i {@link #createObjectOnTile(Class, InitialConditions)}.
     * @param ic {@link InitialConditions} startparametrene for objektet.
     */
    private void initializeEntity(InitialConditions ic) {

        String objectType = ic.getObject();

        switch (objectType) {
            case "rabbit" -> createObjectOnTile(Rabbit.class, ic);
            case "burrow" -> createObjectOnTile(RabbitHole.class, ic);
            case "grass" -> createObjectOnTile(Grass.class, ic);
            case "wolf" -> createObjectOnTile(WolfPack.class, ic);
            case "bear" -> createObjectOnTile(Bear.class, ic);
            case "carcass" -> createObjectOnTile(Cadavar.class,ic);
            case "berry" -> createObjectOnTile(BerryBush.class,ic);
            default -> System.out.println("could not determine type " + objectType);
        }
    }

    /**
     * Starter simulationen efter at startparametrene for verdenen er blevet initaliseret.
     */
    private void runSimulation() {
        program.show();
        for (int i = 1; i <= simulationStepLength; i++) {
            program.simulate();
        }
    }

    /**
     * Laver den {@link TestPackage} som returneres ved en simulations afslutning.
     * @return {@link TestPackage} som indeholder slutparametrene.
     */
    private TestPackage createTestPackage() {
        return new TestPackage(world, program, world.getEntities());
    }

    /**
     * Står for at instanisere og placere vores {@link itumulator.simulator.Actor} og {@link NonBlocking} objekter på spillefladen.
     * @param objectType er den specifikke klassetype, som der ønskes at blive instansieret.
     * @param ic er startparametrene for klassen.
     */
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

    /**
     * Ved endt simulation, lukker denne metode {@link itumulator.display.Frame}.
     */
    private void stopSimulation() {
        program.getFrame().dispose();
    }

    /**
     * Returnere det aktive {@link Program}.
     * @return {@link Program} som er aktivt.
     */
    public Program getProgram() {
        return program;
    }

    /**
     * Metode for at få en tilfældig lokation fra spillefladen. Tager ikke højde for, hvorvidt der allerede er et objekt på spillefladen.
     * @return Den {@link Location} der er tilfældigt genereret.
     */
    public Location getRandomLocation() {
        int x = rd.nextInt(worldSize);
        int y = rd.nextInt(worldSize);
        return new Location(x, y);
    }
}
