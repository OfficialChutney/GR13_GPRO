import java.awt.Color;
import java.util.Random;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class GrassWorldTest {
    public static void main(String[] args) {
        int size = 15;
        Program p = new Program(size, 800, 1000);
        World w = p.getWorld();

        DisplayInformation di = new DisplayInformation(Color.green,"grass");
        p.setDisplayInformation(Grass.class, di);

        Location place = new Location(size/2, size/2);
        Grass grass = new Grass(w,place,0.5f);
        w.setTile(place, grass);

        p.show();
        for (int i = 0; i < 200; i++) {
            p.simulate(); // runs 200 rounds; 'act' is called 200 times for all placed actors
        }

    }
}
