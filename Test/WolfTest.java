import animal.*;
import domainmodel.Helper;
import hole.RabbitHole;
import hole.WolfHole;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class WolfTest extends TestClass {


    @Test
    public void maxedOutPlayingField() {
        //ASSERT
        int x = 0;
        int y = 0;
        WolfPack wp = new WolfPack(numberOfTiles, new Location(7, 7), world);
        ArrayList<Wolf> wolves = wp.getWolves();

        for (Wolf wolf : wolves) {
            wolf.setCanDie(false);
            wolf.setCanGetPregnant(false);
        }


        //ACT & ASSERT
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 25; i++) {
                program.simulate();
            }
        });

        //ASSERT
        int actual = getObjectsOnMap(Wolf.class).size();
        int expected = numberOfTiles;

        assertEquals(expected, actual);

    }

    @Test
    public void hasBirthed() {
        Program program = new Program(2, display_size, delay);
        World world = program.getWorld();
        Helper.setDisplayInfo(program);
        //ARRANGE
        Location spawnLocation = new Location(0, 0);

        WolfPack wp = new WolfPack(2, spawnLocation, world);

        ArrayList<Wolf> wolves = wp.getWolves();
        Wolf femaleWolf = wolves.get(0);
        femaleWolf.setSex(Sex.FEMALE);
        femaleWolf.setCanDie(false);
        femaleWolf.setChanceToGetPregnant(1f);
        femaleWolf.setAge(20);

        Wolf maleWolf = wolves.get(1);
        maleWolf.setSex(Sex.MALE);
        maleWolf.setCanDie(false);
        maleWolf.setAge(20);
        int i = 0;
        while (i < 7) {
            i = program.getSimulator().getSteps();

            if (i == 3) {
                world.setNight();
            }
            if (i == 6) {
                world.setDay();
            }

            program.simulate();
        }

        //ASSERT
        assertTrue(getObjectsOnMap(Wolf.class, world).size() > 2);
    }

    @Test
    public void wolvesCanDie() {
        //ARRANGE
        Location wolfStartLocation = new Location(0, 0);
        WolfPack wp = new WolfPack(1, wolfStartLocation, world);
        Wolf wolf = wp.getWolves().get(0);
        wolf.setEnergy(1);

        int numOfWolvesStart = getObjectsOnMap(Wolf.class).size();

        program.simulate();

        int numOfWolvesEnd = getObjectsOnMap(Wolf.class).size();

        assertTrue(numOfWolvesStart > numOfWolvesEnd);

        assertEquals(0, numOfWolvesEnd);
    }

    @Test
    public void wolfAttacksBearAndEatsCadaver() {
        Program program = new Program(2, display_size, delay);
        World world = program.getWorld();
        Helper.setDisplayInfo(program);
        Helper.setSimulator(program.getSimulator());


        Location wolfStartLocation = new Location(0, 0);
        WolfPack wp = new WolfPack(1, wolfStartLocation, world);
        Wolf wolf = wp.getWolves().get(0);
        wolf.setCanDie(false);
        wolf.setCanEnergyDecrease(false);
        wolf.setEnergy(1);

        Bear bear = new Bear(world);
        bear.setHitpoints(1);
        Location bearLoc = new Location(0, 1);
        world.setTile(bearLoc, bear);

        int numOfBearsStart = getObjectsOnMap(Bear.class, world).size();
        int wolfEnergyStart = wolf.getEnergy();
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
        int wolfEnergyEnd = wolf.getEnergy();

        assertTrue(wolfEnergyStart < wolfEnergyEnd);
        assertTrue(numOfBearsStart > numOfBearsEnd);
        assertTrue(thereWasCadaver);
    }

    @Test
    public void wolfAttacksRabbitAndEatsCadaver() {
        Program program = new Program(2, display_size, delay);
        World world = program.getWorld();
        Helper.setDisplayInfo(program);


        Location wolfStartLocation = new Location(0, 0);
        WolfPack wp = new WolfPack(1, wolfStartLocation, world);
        Wolf wolf = wp.getWolves().get(0);
        wolf.setCanDie(false);
        wolf.setCanEnergyDecrease(false);
        wolf.setEnergy(1);

        Rabbit rabbit = new Rabbit(world);
        rabbit.setHitpoints(1);
        Location rabbitLoc = new Location(0, 1);
        world.setTile(rabbitLoc, rabbit);

        int numOfRabbitsStart = getObjectsOnMap(Rabbit.class, world).size();
        int wolfEnergyStart = wolf.getEnergy();
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
        int wolfEnergyEnd = wolf.getEnergy();

        assertTrue(wolfEnergyStart < wolfEnergyEnd);
        assertTrue(numOfRabbitsStart > numOfRabbitsEnd);
        assertTrue(thereWasCadaver);
    }

    @Test
    public void wolfAttacksWolfOutSideOfPackAndEatsCadaver() {
        Program program = new Program(2, display_size, delay);
        World world = program.getWorld();
        Helper.setDisplayInfo(program);


        Location wolfStartLocation = new Location(0, 0);
        WolfPack wp = new WolfPack(1, wolfStartLocation, world);
        Wolf wolf = wp.getWolves().get(0);
        wolf.setCanDie(false);
        wolf.setCanEnergyDecrease(false);
        wolf.setEnergy(1);

        Location wolfToDieStartLocation = new Location(0, 1);
        WolfPack wp2 = new WolfPack(1, wolfToDieStartLocation, world);
        Wolf wolfToDie = wp2.getWolves().get(0);
        wolfToDie.setHitpoints(1);

        System.out.println("Primary wolf ID: " + wolf.getWolfPackID());
        System.out.println("Wolf to die ID: " + wolfToDie.getWolfPackID());


        int numOfWolvesStart = getObjectsOnMap(Wolf.class, world).size();
        int wolfEnergyStart = wolf.getEnergy();
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
        int wolfEnergyEnd = wolf.getEnergy();

        assertTrue(wolfEnergyStart < wolfEnergyEnd);
        assertTrue(numOfWolvesStart > numOfWolvesEnd);
        assertTrue(thereWasCadaver);
    }

    @Test
    public void wolfDoesNotAttackWolfFromSamePack() {
        Location leaderLoc = new Location(0, 0);
        WolfPack wp = new WolfPack(2, leaderLoc, world);

        Wolf wolf1 = wp.getWolves().get(0);
        Wolf wolf2 = wp.getWolves().get(1);

        wolf1.setCanDie(false);
        wolf2.setCanDie(false);


        int numOfStartingWolves = getObjectsOnMap(Wolf.class).size();

        for (int i = 0; i < 20; i++) {
            program.simulate();
        }

        int numOfEndingWolves = getObjectsOnMap(Wolf.class).size();

        assertEquals(numOfStartingWolves, numOfEndingWolves);
        assertEquals(wolf1.getWolfPackID(), wolf2.getWolfPackID());

    }

    @Test
    public void isApartOfSamePack() {
        Location leaderLoc = new Location(4, 4);
        WolfPack wp = new WolfPack(6, leaderLoc, world);

        program.simulate();

        Map<Object, Location> entities = world.getEntities();
        ArrayList<Wolf> wolves = new ArrayList<>();
        for (Object entity : entities.keySet()) {
            if (entity instanceof Wolf w) {
                wolves.add(w);
            } else {
                assertDoesNotThrow(() -> {
                    throw new IllegalStateException("Objects other than wolves detected");
                });
            }
        }

        boolean isSamePack = true;
        for (Wolf wolfToCompare : wolves) {

            for (Wolf wolfToCheck : wolves) {
                if (wolfToCompare.getWolfPackID() != wolfToCheck.getWolfPackID()) {
                    isSamePack = false;
                    break;
                }
            }
            if (!isSamePack) {
                break;
            }
        }

        assertTrue(isSamePack);

    }

    @Test
    public void wolvesAreCloseToLeader() {
        Location leaderLoc = new Location(4, 4);
        WolfPack wp = new WolfPack(2, leaderLoc, world);
        ArrayList<Wolf> wolves = wp.getWolves();
        Wolf leaderWolf = wp.getLeaderWolf();
        wolves.remove(leaderWolf);
        leaderWolf.setCanDie(false);

        boolean isInRangeOfLeader = true;

        for (int i = 0; i < 20; i++) {
            program.simulate();
            world.setDay();
            Location leaderWolfLoc = world.getLocation(leaderWolf);

            for (Wolf wolf : wolves) {
                wolf.setCanDie(false);
                Location locOfWolf = world.getLocation(wolf);


                float rangeFromLeader = pythagoras(leaderWolfLoc, locOfWolf);

                if (rangeFromLeader > 4) {
                    isInRangeOfLeader = false;
                    break;
                }

            }

            if (!isInRangeOfLeader) {
                break;
            }


        }

        assertTrue(isInRangeOfLeader);


    }

    @Test
    public void wolfPackDigsDenAndSleepsInSameDen() {
        Location locOfLeader = new Location(4, 4);
        WolfPack wp = new WolfPack(8, locOfLeader, world);
        ArrayList<Wolf> wolves = wp.getWolves();

        for (Wolf wolf : wolves) {
            wolf.setCanDie(false);
        }

        //ACT
        for (int i = 0; i < 10; i++) {
            world.setNight();
            program.simulate();
        }

        for (Wolf wolf : wolves) {

            try {
                world.getLocation(wolf);
            } catch (IllegalArgumentException e) {
                assertThrows(IllegalArgumentException.class, () -> {
                    throw e;
                });
                assertEquals("Object is not on the map.", e.getMessage());
            }

        }

        LinkedList<Object> wolfHoles = getObjectsOnMap(WolfHole.class);

        assertEquals(1, wolfHoles.size());



    }





}
