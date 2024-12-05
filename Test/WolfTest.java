import animal.Rabbit;
import animal.Wolf;
import animal.WolfPack;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WolfTest extends TestClass {




    @Test
    public void maxedOutPlayingField() {
        //ASSERT
        int x = 0;
        int y = 0;
        WolfPack wp = new WolfPack(numberOfTiles, new Location(7,7), world);
        ArrayList<Wolf> wolves = wp.getWolves();

        for (Wolf wolf : wolves) {
            wolf.setCanDie(false);
            wolf.setCanGetPregnant(false);
        }

        program.show();

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



}
