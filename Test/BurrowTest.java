import animal.Bear;
import foliage.Grass;
import hole.RabbitHole;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test af {@link RabbitHole} klassen.
 */
public class BurrowTest extends TestClass {



    public BurrowTest() {
        super();
    }

    /**
     * Test af om et {@link animal.Animal} kan stå på et {@link RabbitHole} uden fejl.
     * Test af krav K1-3b.
     */
    @Test
    public void animalStandOnBurrow() {
        Location rabbitHoleLoc = new Location(4,4);
        RabbitHole rabbitHole = new RabbitHole(world, rabbitHoleLoc);
        world.setTile(rabbitHoleLoc, rabbitHole);

        Bear bear = new Bear(world);
        assertDoesNotThrow(() -> {
            world.setTile(rabbitHoleLoc, bear);
        });

        Location locOfBear = world.getLocation(bear);
        Location locOfRabbitHole = world.getLocation(rabbitHole);

        assertEquals(locOfRabbitHole, locOfBear);

    }



}
