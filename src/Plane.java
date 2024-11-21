import itumulator.executable.Program;
import itumulator.world.World;

import java.util.HashMap;

public class Plane {

    Program program;
    World world;
    int displaySize;
    int delay;


    public Plane() {
        displaySize = 800;
        delay = 1000;
    }

    public void start(int worldSize, HashMap<String, String> initialConditions) {
        program = new Program(worldSize, displaySize, delay);
        world = program.getWorld();

        program.show();

        for (int i = 0; i < 20; i++) {
            program.simulate();
        }


    }

}
