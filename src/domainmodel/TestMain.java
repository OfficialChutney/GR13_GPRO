package domainmodel;

import animal.Rabbit;
import animal.Wolf;
import animal.WolfPack;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;

public class TestMain {

    public static void main(String[] args) {
        Program p = new Program(15,800,1000);

        World world = p.getWorld();


        DisplayInformation di = new DisplayInformation(Color.BLACK, "wolf");
        p.setDisplayInformation(Wolf.class, di);

        Rabbit rb = new Rabbit(world);
        world.setTile(new Location(0,0),rb);
        p.show();




    }

}
