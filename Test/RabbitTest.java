import animal.LifeStage;
import animal.herbivore.Rabbit;
import animal.Sex;
import domainmodel.*;
import foliage.Grass;
import hole.RabbitHole;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RabbitTest extends TestClass {

    RabbitTest() {
        super();
    }

    /**
     * Stress test af {@link Rabbit} klassen.
     * Tilhører ikke et formelt krav.
     */
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
        int actual = getObjectsOnMap(Rabbit.class).size();
        int expected = numberOfTiles;

        assertEquals(expected, actual);

    }
    /**
     * Test om {@link Rabbit} kan formere sig.
     * Test af krav K1-2e.
     */
    @Test
    public void hasBirthed() {
        Program program = new Program(2,display_size, delay);
        World world = program.getWorld();
        Helper.setDisplayInfo(program);
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

        int i = 0;
        program.show();
        while(i < 30) {
            i = program.getSimulator().getSteps();

            if(i == 3) {
                world.setNight();
            }
            if(i == 6) {
                world.setDay();
            }

            program.simulate();
        }
        //ASSERT
        assertTrue(getObjectsOnMap(Rabbit.class,world).size() > 2);
    }
    /**
     * Test om {@link Rabbit} kan bevæge sig.
     * Ikke test af et formelt krav.
     */
    @Test
    public void rabbitHasMoved() {
        //ARRANGE
        int startY = 0;
        int startX = 0;
        Location startLocation = new Location(startX,startY);
        Rabbit rabbit = new Rabbit(world);

        world.setTile(startLocation,rabbit);

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
    /**
     * Test om {@link Rabbit} kun bevæger sig 1 felt af gangen.
     * Ikke test af et formelt krav.
     */
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
        int actual = Math.max(deltaX, deltaY);

        assertEquals(expected, actual);

    }
    /**
     * Test om {@link Rabbit} kan spise {@link Grass} og få energi af det.
     * Test af krav K1-2c.
     */
    @Test
    public void hasEatenGrass() {
        //ARRANGE
        int worldSize = 5;
        Program program = new Program(worldSize,display_size,delay);
        Helper.setDisplayInfo(program);
        World world = program.getWorld();

        Location rabbitStartLocation = new Location(0,0);
        Rabbit rabbit = new Rabbit(world);
        world.setTile(rabbitStartLocation, rabbit);

        Location grassLocation = new Location(worldSize-1,worldSize-1);
        Grass grass = new Grass(world);
        grass.setCanSpread(false);
        world.setTile(grassLocation,grass);

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
    /**
     * Test om {@link Rabbit} kan dø.
     * Test af krav K1-2b.
     */
    @Test
    public void rabbitsCanDie() {
        //ARRANGE
        Location rabbitStartLocation = new Location(0,0);
        Rabbit rabbit = new Rabbit(world);
        world.setTile(rabbitStartLocation, rabbit);
        rabbit.setEnergy(1);

        int numOfRabbitsStart = getObjectsOnMap(Rabbit.class).size();

        program.simulate();

        int numOfRabbitsEnd = getObjectsOnMap(Rabbit.class).size();

        assertTrue(numOfRabbitsStart > numOfRabbitsEnd);

        assertEquals(0, numOfRabbitsEnd);
    }
    /**
     * Test om {@link Rabbit} max energi påvirkes af deres age.
     * Test af krav K1-2d.
     */
    @Test
    public void rabbitAgeChangeMaxEnergy() {
        //ARRANGE
        Location rabbitStartLocation = new Location(0,0);
        Rabbit rabbit = new Rabbit(world);
        rabbit.setCanDie(false);
        int defaultMaxEnergy = rabbit.getMaxEnergy();

        world.setTile(rabbitStartLocation, rabbit);

        //ACT
        rabbit.setAge(2);
        program.simulate();
        int rabbitMaxEnergyStart = rabbit.getMaxEnergy();

        rabbit.setAge(10);
        program.simulate();
        int rabbitMaxEnergyEnd = rabbit.getMaxEnergy();


        assertTrue(rabbitMaxEnergyStart > rabbitMaxEnergyEnd);

        assertEquals(defaultMaxEnergy - 2, rabbitMaxEnergyStart);
        assertEquals(defaultMaxEnergy - 10, rabbitMaxEnergyEnd);
    }
    /**
     * Test om {@link Rabbit} kan grave et {@link RabbitHole}.
     * Test af krav K1-2f og K1-3a.
     */
    @Test
    public void hasDugHole() {
        //ARRANGE
        Location rabbitStartLocation = new Location(0,0);
        Rabbit rabbit = new Rabbit(world);
        rabbit.setCanDie(false);
        world.setTile(rabbitStartLocation, rabbit);
        world.setNight();
        int numOfHolesBeforeAct = getObjectsOnMap(RabbitHole.class).size();

        //ACT
        for (int i = 0; i < 4; i++) {
            program.simulate();
        }

        int expected = 1;
        int actual = getObjectsOnMap(RabbitHole.class).size();

        assertEquals(expected, actual);

        expected = 0;
        actual = numOfHolesBeforeAct;
        assertEquals(expected, actual);



    }
    /**
     * Test om {@link Rabbit} søger mod deres huller.
     * Test af krav K1-2g.
     */
    @Test
    public void hasReachedHole() {
        //ARRANGE
        Location rabbitStartLocation = new Location(0,0);
        Rabbit rabbit = new Rabbit(world);
        rabbit.setCanDie(false);
        world.setTile(rabbitStartLocation, rabbit);
        world.setNight();

        //ACT
        for (int i = 0; i < 4; i++) {
            program.simulate();
        }

        int expected = 1;
        int actual = getObjectsOnMap(RabbitHole.class).size();

        assertEquals(expected, actual);

        try {
            world.getLocation(rabbit);
        } catch (IllegalArgumentException e) {
            assertThrows(IllegalArgumentException.class, () -> {
               throw e;
            });
            assertEquals("Object is not on the map.", e.getMessage());
        }


        assertEquals(rabbitStartLocation.getX(), rabbit.getMyRabbitHole().getTileLocation().getX());
        assertEquals(rabbitStartLocation.getY(), rabbit.getMyRabbitHole().getTileLocation().getY());


    }



}