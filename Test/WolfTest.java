import animal.*;
import domainmodel.Helper;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
        while(i < 7) {
            i = program.getSimulator().getSteps();

            if(i == 3) {
                world.setNight();
            }
            if(i == 6) {
                world.setDay();
            }

            program.simulate();
        }

        //ASSERT
        assertTrue(getObjectsOnMap(Wolf.class, world).size() > 2);
    }




}
