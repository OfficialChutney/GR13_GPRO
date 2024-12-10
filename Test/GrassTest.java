import animal.carnivores.Bear;
import foliage.Grass;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GrassTest extends TestClass {


    /**
     * Test af om {@link Grass} kan sprede sig.
     * Test af krav K1-1b.
     */
    @Test
    public void grassCanSpread() {

        Grass grass = new Grass(world);
        grass.setChanceToGrow(1f);
        Location grassLocation = new Location(4,4);
        world.setTile(grassLocation, grass);

        int numOfGrassBeforeSim = getObjectsOnMap(Grass.class).size();
        program.simulate();

        int numOfGrassAfterSim = getObjectsOnMap(Grass.class).size();

        assertTrue(numOfGrassBeforeSim < numOfGrassAfterSim);
    }
    /**
     * Test af om et {@link animal.Animal} kan stå på et {@link Grass} uden fejl.
     * Test af krav K1-1c.
     */
    @Test
    public void animalStandOnGrass() {
        Grass grass = new Grass(world);
        Location grassLocation = new Location(4,4);
        world.setTile(grassLocation, grass);

        Bear bear = new Bear(world);
        assertDoesNotThrow(() -> {
            world.setTile(grassLocation, bear);
        });

        Location locOfBear = world.getLocation(bear);
        Location locOfGrass = world.getLocation(grass);

        assertEquals(locOfGrass, locOfBear);


    }


}
