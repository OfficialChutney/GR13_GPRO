package domainmodel;

import animal.Rabbit;
import animal.Wolf;
import animal.WolfPack;
import hole.RabbitHole;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import hole.WolfHole;

import java.awt.*;

public class TestMain {

    public static void main(String[] args) {
        Program p = new Program(15,800,300);

        World world = p.getWorld();


        DisplayInformation di = new DisplayInformation(Color.BLACK, "wolf");
        p.setDisplayInformation(Wolf.class, di);

        DisplayInformation wolfHole = new DisplayInformation(Color.BLACK, "hole");
        p.setDisplayInformation(WolfHole.class, wolfHole);

        WolfPack wp = new WolfPack(5, new Location(3,3), world);


        p.show();

        for (int i = 0; i < 50; i++) {
            p.simulate();
        }


    }

}
