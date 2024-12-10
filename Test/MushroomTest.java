import animal.Cadavar;
import domainmodel.Helper;
import foliage.Mushroom;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test af {@link Mushroom} klassen.
 */
public class MushroomTest extends TestClass {

    /**
     * Test om et {@link Mushroom} kan sprede sig til andre {@link Cadavar} uden fungi.
     * Svare til test af krav K3-2b.
     */
    @Test
    public void mushroomCanSpreadToCadaver() {
        Mushroom mushroom = new Mushroom(world, 100);
        Location locOfMushroom = new Location(1,1);
        world.setTile(locOfMushroom, mushroom);

        ArrayList<Location> locationsForCadaver = new ArrayList<>();

        locationsForCadaver.add(new Location(0,0));
        locationsForCadaver.add(new Location(0,1));
        locationsForCadaver.add(new Location(1,0));
        locationsForCadaver.add(new Location(2,2));
        locationsForCadaver.add(new Location(2,1));
        locationsForCadaver.add(new Location(1,2));

        for (Location loc : locationsForCadaver) {
            world.setTile(loc, new Cadavar(world, false, 20,10));
        }


        int numOfMushroomsStart = getObjectsOnMap(Mushroom.class).size();
        int numOfCadavarStart = getObjectsOnMap(Cadavar.class).size();


        for (int i = 0; i < 20; i++) {
            program.simulate();
        }

        int numOfMushroomsEnd = getObjectsOnMap(Mushroom.class).size();
        int numOfCadavarEnd = getObjectsOnMap(Cadavar.class).size();

        assertTrue(numOfMushroomsStart < numOfMushroomsEnd);
        assertTrue(numOfCadavarStart > numOfCadavarEnd);


    }
    /**
     * Test om et {@link Mushroom} fra et større {@link Cadavar} dør senere end en {@link Mushroom} fra et mindre {@link Cadavar}.
     * Svare til test af krav K3-2b.
     */
    @Test
    public void biggerCavadarMushroomsDecomposeLater() {
        Cadavar biggerCadaver = new Cadavar(world,true,30,10);
        Cadavar smallerCadaver = new Cadavar(world,true,10,10);

        Location locOfBig = new Location(0,0);
        Location locOfSmall = new Location(1,1);

        world.setTile(locOfBig,biggerCadaver);
        world.setTile(locOfSmall,smallerCadaver);

        int numOfMushroomsStart = getObjectsOnMap(Mushroom.class).size();;
        int numOfCadavarsStart = getObjectsOnMap(Cadavar.class).size();

        for (int i = 0; i < 30; i++) {
            program.simulate();
            if(Helper.getSteps() == 10) {
                numOfMushroomsStart = getObjectsOnMap(Mushroom.class).size();
            }
        }

        int numOfMushroomsEnd = getObjectsOnMap(Mushroom.class).size();
        int numOfCadavarsEnd = getObjectsOnMap(Cadavar.class).size();

        assertTrue(numOfMushroomsStart > numOfMushroomsEnd);
        assertEquals(1, numOfMushroomsEnd);
        assertTrue(numOfCadavarsStart > numOfCadavarsEnd);
        assertNull(world.getNonBlocking(locOfSmall));
        assertInstanceOf(Mushroom.class, world.getNonBlocking(locOfBig));



    }


}
