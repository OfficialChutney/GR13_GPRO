package domainmodel;

import animal.Cadavar;
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
        Program p = new Program(7, 800, 600);

        World world = p.getWorld();


        DisplayInformation di = new DisplayInformation(Color.BLACK, "wolf");
        p.setDisplayInformation(Wolf.class, di);

        DisplayInformation wolfHole = new DisplayInformation(Color.BLACK, "hole");
        p.setDisplayInformation(WolfHole.class, wolfHole);

        DisplayInformation rabbitHole = new DisplayInformation(Color.BLACK, "hole");
        p.setDisplayInformation(RabbitHole.class, rabbitHole);

        DisplayInformation cadavar = new DisplayInformation(Color.BLACK);
        p.setDisplayInformation(Cadavar.class, cadavar);

        Rabbit rabbit = new Rabbit(world);
        world.setTile(new Location(1, 1), rabbit);

        WolfPack wp1 = new WolfPack(4, new Location(5, 5), world);

        WolfPack wp2 = new WolfPack(4, new Location(3, 3), world);


        p.show();

        for (int i = 0; i < 100; i++) {
            p.simulate();
        }


    }

}
