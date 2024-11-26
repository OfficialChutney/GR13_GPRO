package domainmodel;

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

        WolfPack wp = new WolfPack(10, new Location(7,7), world);

        p.show();



    }

}
