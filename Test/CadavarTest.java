import animal.*;
import domainmodel.Helper;
import foliage.Mushroom;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link Cadavar} klassen.
 */

public class CadavarTest extends TestClass {

    /**
     * Test for om {@link Rabbit} efterlader ådsler ved død. Svare til test af krav K3-1b
     */
    @Test
    public void rabbitLeavesCadaver() {
        Rabbit rb = new Rabbit(world);
        Location loc = new Location(0,0);
        rb.setEnergy(1);
        world.setTile(loc, rb);

        int numOfRabbitsStart = getObjectsOnMap(Rabbit.class).size();
        int numOfCadavarsStart = getObjectsOnMap(Cadavar.class).size();

        for (int i = 0; i < 20; i++) {
            program.simulate();
        }

        int numOfRabbitsEnd = getObjectsOnMap(Rabbit.class).size();
        int numOfCadavarsEnd = getObjectsOnMap(Cadavar.class).size();

        assertTrue(numOfRabbitsStart > numOfRabbitsEnd);
        assertTrue(numOfCadavarsStart < numOfCadavarsEnd);
    }
    /**
     * Test for om {@link Bear} efterlader ådsler ved død. Svare til test af krav K3-1b
     */
    @Test
    public void BearLeavesCadaver() {
        Bear bear = new Bear(world);
        Location loc = new Location(0,0);
        bear.setEnergy(1);
        world.setTile(loc, bear);

        int numOfBearsStart = getObjectsOnMap(Bear.class).size();
        int numOfCadavarsStart = getObjectsOnMap(Cadavar.class).size();

        for (int i = 0; i < 20; i++) {
            program.simulate();
        }

        int numOfBearsEnd = getObjectsOnMap(Bear.class).size();
        int numOfCadavarsEnd = getObjectsOnMap(Cadavar.class).size();

        assertTrue(numOfBearsStart > numOfBearsEnd);
        assertTrue(numOfCadavarsStart < numOfCadavarsEnd);
    }

    /**
     * Test for om {@link Wolf} efterlader ådsler ved død. Svare til test af krav K3-1b
     */
    @Test
    public void WolfLeavesCadaver() {
        Location loc = new Location(0,0);
        WolfPack wp = new WolfPack(1, loc, world);
        Wolf wolf = wp.getWolves().getFirst();
        wolf.setEnergy(1);

        int numOfWolvesStart = getObjectsOnMap(Wolf.class).size();
        int numOfCadavarsStart = getObjectsOnMap(Cadavar.class).size();

        for (int i = 0; i < 20; i++) {
            program.simulate();
        }

        int numOfWolvesEnd = getObjectsOnMap(Wolf.class).size();
        int numOfCadavarsEnd = getObjectsOnMap(Cadavar.class).size();

        assertTrue(numOfWolvesStart > numOfWolvesEnd);
        assertTrue(numOfCadavarsStart < numOfCadavarsEnd);
    }

    /**
     * Test af om {@link Cadavar} forsvinder over tid. Svare til krav K3-1c.
     */
    @Test
    public void CadaverDecayesOverTime() {
        Cadavar cadaver = new Cadavar(world, false, 160, 20);
        Location loc = new Location(0,0);
        world.setTile(loc, cadaver);

        int numOfCadavarsStart = getObjectsOnMap(Cadavar.class).size();


        for (int i = 0; i < 30; i++) {
            program.simulate();
        }

        int numOfCadavarsEnd = getObjectsOnMap(Cadavar.class).size();

        assertTrue(numOfCadavarsStart > numOfCadavarsEnd);
    }

    /**
     * Test om et {@link Cadavar} der forsvinder over tid bliver til en {@link Mushroom}.
     * Svare til test af krav K3-2a.
     */
    @Test
    public void CadaverDecayesOverTimeAndProducesMushroom() {
        Cadavar cadaver = new Cadavar(world, true, 160, 20);
        Location loc = new Location(0,0);
        world.setTile(loc, cadaver);

        int numOfCadavarsStart = getObjectsOnMap(Cadavar.class).size();
        int numOfMushroomsStart = getObjectsOnMap(Mushroom.class).size();

        for (int i = 0; i < 30; i++) {
            program.simulate();
        }

        int numOfCadavarsEnd = getObjectsOnMap(Cadavar.class).size();
        int numOfMushroomsEnd = getObjectsOnMap(Mushroom.class).size();


        assertTrue(numOfCadavarsStart > numOfCadavarsEnd);
        assertTrue(numOfMushroomsStart < numOfMushroomsEnd);

    }
    /**
     * Test af om {@link Cadavar} forsvinder såfremt de bliver spist, før deres decay time.
     * Svare til krav K3-1c.
     */
    @Test
    public void CadaverCanBeEatenBeforeDecay() {
        Cadavar cadaver = new Cadavar(world, false, 20, 30);
        Location loc = new Location(0,0);
        world.setTile(loc, cadaver);
        Bear bear = new Bear(world);
        Location locBear = new Location(0,1);
        world.setTile(locBear, bear);

        int numOfCadavarsStart = getObjectsOnMap(Cadavar.class).size();
        boolean eatenBeforeDecayTime = false;

        for (int i = 0; i < 60; i++) {
            program.simulate();
            int numOfCadavers = getObjectsOnMap(Cadavar.class).size();
            if(numOfCadavers == 0 && Helper.getSteps() < 30) {
                eatenBeforeDecayTime = true;
                break;
            }

        }

        int numOfCadavarsEnd = getObjectsOnMap(Cadavar.class).size();

        assertTrue(numOfCadavarsStart > numOfCadavarsEnd);
        assertTrue(eatenBeforeDecayTime);
    }


}
