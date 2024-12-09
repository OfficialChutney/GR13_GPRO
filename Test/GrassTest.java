import animal.Bear;
import animal.Rabbit;
import foliage.Grass;
import hole.RabbitHole;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.Map;

public class GrassTest extends TestClass {


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
