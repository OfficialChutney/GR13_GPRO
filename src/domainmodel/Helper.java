package domainmodel;

import animal.Bear;
import animal.Cadavar;
import animal.Rabbit;
import animal.Wolf;
import foliage.Grass;
import hole.RabbitHole;
import hole.WolfHole;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Simulator;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.Set;

public class Helper {

    private static Simulator sim;



    public static Set<Location> getEmptySurroundingTiles(World world, Location location, int radius) {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location, radius);
        surroundingTiles.removeIf(tile -> !world.isTileEmpty(tile));
        return surroundingTiles;
    }

    public static void setDisplayInfo(Program program) {

        //Set display for Grass
        DisplayInformation grassDisplay = new DisplayInformation(Color.black, "grass");
        program.setDisplayInformation(Grass.class, grassDisplay);

        //Set display for Rabbitholes
        DisplayInformation rabbitHoleDisplay = new DisplayInformation(Color.orange, "hole-small");
        program.setDisplayInformation(RabbitHole.class, rabbitHoleDisplay);

        //Set display for Rabbit
        DisplayInformation rabbitDisplay = new DisplayInformation(Color.orange, "rabbit-large");
        program.setDisplayInformation(Rabbit.class, rabbitDisplay);

        //Set display for Wolf
        DisplayInformation wolfDisplay = new DisplayInformation(Color.orange, "wolf");
        program.setDisplayInformation(Wolf.class, wolfDisplay);

        //Set display for Bear
        DisplayInformation BearDisplay = new DisplayInformation(Color.orange, "bear");
        program.setDisplayInformation(Bear.class, BearDisplay);

        //Set display for WolfHole
        DisplayInformation WolfHole = new DisplayInformation(Color.orange, "hole");
        program.setDisplayInformation(WolfHole.class, WolfHole);

        //Set display for Cadaver
        DisplayInformation Cadaver = new DisplayInformation(Color.orange, "carcass");
        program.setDisplayInformation(Cadavar.class, Cadaver);

    }

    public static void setSimulator(Simulator sim) {
        Helper.sim = sim;
    }

    public static int getSteps() {
        return sim.getSteps();
    }


}
