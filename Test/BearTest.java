import animal.*;
import animal.carnivores.Bear;
import animal.carnivores.Wolf;
import animal.carnivores.WolfPack;
import animal.herbivore.Rabbit;
import domainmodel.Helper;
import foliage.BerryBush;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BearTest extends TestClass {

    @Test
    public void maxedOutPlayingField() {
        //ASSERT
        int x = 0;
        int y = 0;
        for (int i = 0; i < numberOfTiles; i++) {
            x = i % worldSize;
            y = y + ((x == 0 && i > 0) ? 1 : 0);
            Bear bear = new Bear(world);
            bear.setCanDie(false);
            bear.setCanGetPregnant(false);
            Location loc = new Location(x, y);

            //ACT & ASSERT
            assertDoesNotThrow(() -> {
                world.setTile(loc, bear);
            });
        }

        //ACT & ASSERT
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 25; i++) {
                program.simulate();
            }
        });

        //ASSERT
        int actual = getObjectsOnMap(Bear.class).size();
        int expected = numberOfTiles;

        assertEquals(expected, actual);

    }

    /**
     * Test om {@link Bear} dræber andre {@link Bear} og spiser det efterladte {@link Cadavar}.
     * Svare til test af krav K3-1b og K2-4b.
     */
    @Test
    public void BearAttacksBearAndEatsCadaver() {
        Program program = new Program(2, display_size, delay);
        World world = program.getWorld();
        Helper.setDisplayInfo(program);
        Helper.setSimulator(program.getSimulator());

        Location bearStartLocation = new Location(0, 0);
        Bear bear = new Bear(world);
        bear.setCanDie(false);
        bear.setCanEnergyDecrease(false);
        bear.setEnergy(1);
        world.setTile(bearStartLocation, bear);

        Bear bearToDie = new Bear(world);
        bear.setHitpoints(1);
        Location bearLoc = new Location(0, 1);
        world.setTile(bearLoc, bearToDie);

        int numOfBearsStart = getObjectsOnMap(Bear.class, world).size();
        int bearEnergyStart = bear.getEnergy();
        boolean thereWasCadaver = false;

        int i = 0;
        while (i < 10) {
            i = program.getSimulator().getSteps();
            program.simulate();
            if (!thereWasCadaver) {
                thereWasCadaver = (!getObjectsOnMap(Cadavar.class, world).isEmpty());
            }

        }

        int numOfBearsEnd = getObjectsOnMap(Bear.class, world).size();
        int bearEnergyEnd = bear.getEnergy();

        assertTrue(bearEnergyStart < bearEnergyEnd);
        assertTrue(numOfBearsStart > numOfBearsEnd);
        assertTrue(thereWasCadaver);
    }
    /**
     * Test om {@link Bear} dræber {@link Rabbit} og spiser det efterladte {@link Cadavar}.
     * Svare til test af krav K3-1b og K2-4b.
     */
    @Test
    public void BearAttacksRabbitAndEatsCadaver() {
        Program program = new Program(2, display_size, delay);
        World world = program.getWorld();
        Helper.setDisplayInfo(program);
        Helper.setSimulator(program.getSimulator());

        Location bearStartLocation = new Location(0, 0);
        Bear bear = new Bear(world);
        bear.setCanDie(false);
        bear.setCanEnergyDecrease(false);
        bear.setEnergy(1);
        world.setTile(bearStartLocation, bear);

        Rabbit rabbit = new Rabbit(world);
        rabbit.setHitpoints(1);
        Location rabbitLoc = new Location(0, 1);
        world.setTile(rabbitLoc, rabbit);

        int numOfRabbitsStart = getObjectsOnMap(Rabbit.class, world).size();
        int bearEnergyStart = bear.getEnergy();
        boolean thereWasCadaver = false;

        int i = 0;
        while (i < 10) {
            i = program.getSimulator().getSteps();
            program.simulate();
            if (!thereWasCadaver) {
                thereWasCadaver = (!getObjectsOnMap(Cadavar.class, world).isEmpty());
            }

        }

        int numOfRabbitsEnd = getObjectsOnMap(Rabbit.class, world).size();
        int bearEnergyEnd = bear.getEnergy();

        assertTrue(bearEnergyStart < bearEnergyEnd);
        assertTrue(numOfRabbitsStart > numOfRabbitsEnd);
        assertTrue(thereWasCadaver);
    }
    /**
     * Test om {@link Bear} dræber {@link Wolf} og spiser det efterladte {@link Cadavar}.
     * Svare til test af krav K3-1b og K2-4b.
     */
    @Test
    public void BearAttacksWolfAndEatsCadaver() {
        Program program = new Program(2, display_size, delay);
        World world = program.getWorld();
        Helper.setDisplayInfo(program);
        Helper.setSimulator(program.getSimulator());

        Location bearStartLocation = new Location(0, 0);
        Bear bear = new Bear(world);
        bear.setCanDie(false);
        bear.setCanEnergyDecrease(false);
        bear.setEnergy(1);
        world.setTile(bearStartLocation, bear);


        Location wolfToDieStartLocation = new Location(0, 1);
        WolfPack wp = new WolfPack(1, wolfToDieStartLocation, world);
        Wolf wolfToDie = wp.getWolves().getFirst();
        wolfToDie.setHitpoints(1);

        int numOfWolvesStart = getObjectsOnMap(Wolf.class, world).size();
        int bearEnergyStart = bear.getEnergy();
        boolean thereWasCadaver = false;


        int i = 0;
        while (i < 40) {
            i = program.getSimulator().getSteps();
            program.simulate();
            if (!thereWasCadaver) {
                thereWasCadaver = (!getObjectsOnMap(Cadavar.class, world).isEmpty());
            }

        }

        int numOfWolvesEnd = getObjectsOnMap(Wolf.class, world).size();
        int bearEnergyEnd = bear.getEnergy();

        assertTrue(bearEnergyStart < bearEnergyEnd);
        assertTrue(numOfWolvesStart > numOfWolvesEnd);
        assertTrue(thereWasCadaver);
    }
    /**
     * Test om {@link Bear} spiser bær fra {@link BerryBush}.
     * Svare til test af krav K2-6a.
     */
    @Test
    public void bearEatsBerriesFromBushes() {
        Program program = new Program(2, display_size, delay);
        World world = program.getWorld();
        Helper.setDisplayInfo(program);
        Helper.setSimulator(program.getSimulator());

        Location bearStartLocation = new Location(0, 0);
        Bear bear = new Bear(world);
        bear.setCanDie(false);
        bear.setCanEnergyDecrease(false);
        bear.setEnergy(1);
        world.setTile(bearStartLocation, bear);

        Location locationOfBerryBush = new Location(0, 1);
        BerryBush berrybush = new BerryBush();
        world.setTile(locationOfBerryBush, berrybush);

        int bearEnergyStart = bear.getEnergy();

        boolean berriesHaveDisappeared = false;
        for (int i = 0; i < 40; i++) {
            program.simulate();
            if (!berriesHaveDisappeared) {
                berriesHaveDisappeared = !berrybush.berryState();
            }
        }

        int bearEnergyEnd = bear.getEnergy();


        assertTrue(bearEnergyStart < bearEnergyEnd);
        assertTrue(berriesHaveDisappeared);

    }

    /**
     * Test om {@link Bear} forbliver i dens territorie.
     * Test af krav K2-5a.
     */
    @Test
    public void bearStaysInTerritory() {

        Location bearStartLocation = new Location(4, 4);
        Bear bear = new Bear(world);
        bear.setCanDie(false);
        world.setTile(bearStartLocation, bear);
        for (int i = 0; i < 100; i++) {
            program.simulate();
            Location currentLoc = world.getLocation(bear);

            float distance = pythagoras(currentLoc, bearStartLocation);
            assertTrue(distance < 5.5);
        }
    }

    /**
     * Test af om {@link Bear} parre sig i parringssæsonen.
     * Ikke noget formelt krav, som dette er en test af.
     */
    @Test
    public void bearMatesInMatingSeason() {
        Program program = new Program(10, display_size, delay);
        World world = program.getWorld();
        Helper.setDisplayInfo(program);
        Helper.setSimulator(program.getSimulator());


        int matingSeason = 40;
        Location locOfMaleBear = new Location(0, 0);
        Bear maleBear = new Bear(world);
        maleBear.setCanDie(false);
        maleBear.setSex(Sex.MALE);
        world.setTile(locOfMaleBear, maleBear);

        Location locOfFemaleBear = new Location(9, 9);
        Bear femaleBear = new Bear(world);
        femaleBear.setCanDie(false);
        femaleBear.setSex(Sex.FEMALE);
        world.setTile(locOfFemaleBear, femaleBear);


        boolean hasMadeBaby = false;
        for (int i = 0; i < 80; i++) {
            int steps = Helper.getSteps();
            program.simulate();
            if (steps < matingSeason) {
                assertTrue(getObjectsOnMap(Bear.class, world).size() <= 2);
            } else {

                if (getObjectsOnMap(Bear.class, world).size() > 2) {
                    hasMadeBaby = true;
                    break;
                }

            }
        }
        assertTrue(hasMadeBaby);

    }

}
