import animal.*;
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

        assertNotEquals(bearEnergyStart, bearEnergyEnd);
        assertNotEquals(numOfBearsStart, numOfBearsEnd);
        assertTrue(thereWasCadaver);
    }

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

        assertNotEquals(bearEnergyStart, bearEnergyEnd);
        assertNotEquals(numOfRabbitsStart, numOfRabbitsEnd);
        assertTrue(thereWasCadaver);
    }

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

        assertNotEquals(bearEnergyStart, bearEnergyEnd);
        assertNotEquals(numOfWolvesStart, numOfWolvesEnd);
        assertTrue(thereWasCadaver);
    }

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

        Location locationOfBerryBush = new Location(0,1);
        BerryBush berrybush = new BerryBush();
        world.setTile(locationOfBerryBush, berrybush);

        int bearEnergyStart = bear.getEnergy();

        boolean berriesHaveDisappeared = false;
        for (int i = 0; i < 40; i++) {
            program.simulate();
            if(!berriesHaveDisappeared) {
                berriesHaveDisappeared = !berrybush.berryState();
            }
        }

        int bearEnergyEnd = bear.getEnergy();



        assertNotEquals(bearEnergyStart, bearEnergyEnd);
        assertTrue(berriesHaveDisappeared);

    }

    @Test
    public void bearStaysInTerritory() {

        Location bearStartLocation = new Location(4, 4);
        Bear bear = new Bear(world);
        bear.setCanDie(false);
        world.setTile(bearStartLocation, bear);
        program.show();
        for (int i = 0; i < 100; i++) {
            program.simulate();
            Location currentLoc = world.getLocation(bear);

            float distance = pythagoras(currentLoc, bearStartLocation);
            System.out.println(distance);
            System.out.println(bear.getBearBehavior());
        }


    }


}
