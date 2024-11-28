import animal.LifeStage;
import animal.Rabbit;
import animal.Sex;
import domainmodel.*;
import foliage.Grass;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import org.junit.jupiter.api.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class RabbitTest {

    private int worldSize;
    private int display_size;
    private int delay;
    private int numberOfTiles;
    private World world;
    private Program program;
    private Helper helper;

    public RabbitTest() {
        helper = new Helper();
        delay = 500;
        display_size = 800;
        worldSize = 15;
        numberOfTiles = worldSize * worldSize;
    }

    @BeforeEach
    public void makeWorld() {
        program = new Program(worldSize, display_size, delay);
        world = program.getWorld();
        helper.setDisplayInfo(program);
    }

    @Test
    public void maxedOutPlayingField() {
        //ASSERT
        int x = 0;
        int y = 0;
        for (int i = 0; i < numberOfTiles; i++) {
            x = i % worldSize;
            y = y + ((x == 0 && i > 0) ? 1 : 0);
            Rabbit rabbit = new Rabbit(world);
            rabbit.setCanDie(false);
            rabbit.setCanGetPregnant(false);
            Location loc = new Location(x, y);

            //ACT & ASSERT
            assertDoesNotThrow(() -> {
                world.setTile(loc, rabbit);
            });
        }

        //ACT & ASSERT
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 25; i++) {
                program.simulate();
            }
        });

        //ASSERT
        int actual = getRabbitsOnMap().size();
        int expected = numberOfTiles;

        assertEquals(expected, actual);

    }

    @Test
    public void hasBirthed() {
        Program program = new Program(2,display_size, delay);
        World world = program.getWorld();
        helper.setDisplayInfo(program);
        //ARRANGE
        Location loc1 = new Location(0,0);
        Location loc2 = new Location(0,1);

        Rabbit maleRabbit = new Rabbit(world);
        maleRabbit.setSex(Sex.MALE);
        maleRabbit.setAge(LifeStage.ADULT.getAge());
        Rabbit femaleRabbit = new Rabbit(world);
        femaleRabbit.setSex(Sex.FEMALE);
        femaleRabbit.setAge(LifeStage.ADULT.getAge());

        world.setTile(loc1, maleRabbit);
        world.setTile(loc2, femaleRabbit);

        program.simulate();
        world.setNight();
        program.simulate();
        world.setDay();
        program.simulate();


        //ASSERT
        assertTrue(getRabbitsOnMap(world).size() > 2);
    }

    @Test
    public void rabbitHasMoved() {
        //ARRANGE
        int startY = 0;
        int startX = 0;
        Location startLocation = new Location(startX,startY);
        Rabbit rabbit = new Rabbit(world);

        world.setTile(startLocation,rabbit);

        program.show();
        //ACT
        for (int i = 0; i < 5; i++) {
            program.simulate();
        }


        Location endLocation = world.getLocation(rabbit);
        int endX = endLocation.getX();
        int endY = endLocation.getY();

        //ASSERT Y COORDINATE HAS CHANGED
        assertTrue(startX != endX || startY != endY);
    }

    @Test
    public void rabbitOnlyMovedOneTile() {
        //ARRANGE
        int startY = 0;
        int startX = 0;
        Location startLocation = new Location(startX,startY);
        Rabbit rabbit = new Rabbit(world);
        world.setTile(startLocation,rabbit);

        //ACT
        program.simulate();

        //ASSERT
        Location endLocation = world.getLocation(rabbit);
        int endX = endLocation.getX();
        int endY = endLocation.getY();

        int deltaX = Math.abs(startX - endX);
        int deltaY = Math.abs(startY - endY);

        int expected = 1;
        int actual = deltaX + deltaY;

        assertEquals(expected, actual);

    }

    @Test
    public void hasEatenGrass() {
        //ARRANGE
        int worldSize = 5;
        Program program = new Program(worldSize,display_size,delay);
        helper.setDisplayInfo(program);
        World world = program.getWorld();

        Location rabbitStartLocation = new Location(0,0);
        Rabbit rabbit = new Rabbit(world);
        world.setTile(rabbitStartLocation, rabbit);

        Location grassLocation = new Location(worldSize-1,worldSize-1);
        Grass grass = new Grass(world);
        grass.setCanSpread(false);
        world.setTile(grassLocation,grass);

        program.show();
        //ACT
        for (int i = 0; i < 10; i++) {
            program.simulate();
        }

        //ASSERT
        Object possibleGrass = world.getNonBlocking(grassLocation);
        boolean actual = possibleGrass instanceof Grass;
        boolean expected = false;

        assertEquals(expected,actual);

    }


    @Test
    public void rabbitPlacedFromFile() {
        //ASSERT

        UserInterface ui = new UserInterface();
        String directory = ui.getInputFileDirectory();
        ui.setTest(true);
        File testFile = new File(directory+"/testFile.txt");
        ui.setSpecifiedFileToRun("testFile.txt");

        int numOfRabbits = 15;

        try {
            testFile.createNewFile();
            try (FileWriter fw = new FileWriter(testFile)) {
                fw.write(String.valueOf(worldSize)+"\n");
                fw.write("rabbit "+String.valueOf(numOfRabbits));
            }

            TestPackage tp = ui.startProgram();

            int expected = numOfRabbits;
            int actual = tp.getEntities().size();


            assertEquals(expected, actual);

        } catch (IOException e) {
            assertDoesNotThrow(() -> {
                throw e;
            });
        }






    }


    private LinkedList<Rabbit> getRabbitsOnMap(World world) {
        Map<Object, Location> entities = world.getEntities();
        LinkedList<Rabbit> rabbits = new LinkedList<>();

        for (Object o : entities.keySet()) {
            if (o instanceof Rabbit r) {
                rabbits.add(r);
            }
        }

        return rabbits;
    }

    private LinkedList<Grass> getGrassOnMap(World world) {
        Map<Object, Location> entities = world.getEntities();
        LinkedList<Grass> grass = new LinkedList<>();

        for (Object o : entities.keySet()) {
            if (o instanceof Grass g) {
                grass.add(g);
            }
        }

        return grass;
    }

    private LinkedList<Rabbit> getRabbitsOnMap() {
        return getRabbitsOnMap(world);
    }

    private LinkedList<Grass> getGrassOnMap() {
        return getGrassOnMap(world);
    }
}
