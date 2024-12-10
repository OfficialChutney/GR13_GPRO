import foliage.BerryBush;
import itumulator.world.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BerryBushTest extends TestClass {


    /**
     * Test af om {@link BerryBush} gror bær ved det korrekte tilfældige interval, med en diskrepant på 0.2.
     * Test af krav K2-6a.
     */
    @Test
    public void BerrysGrowOverTime() {
        BerryBush bb = new BerryBush();
        Location loc = new Location(0,0);

        world.setTile(loc, bb);
        int steps = 40;
        int hasGrown = 0;
        for (int i = 0; i < steps; i++) {
            program.simulate();
            if (bb.berryState()) {
                hasGrown++;
                bb.setBerryState(false);
            }
        }

        float chanceToGrow = 0.2f;
        float actualGrownChance = (float) hasGrown /steps;
        System.out.println(actualGrownChance);

        assertTrue((chanceToGrow - 0.2f) < actualGrownChance && actualGrownChance < (chanceToGrow + 0.2f) );

    }
}
