package domainmodel;

import animal.Bear;
import animal.Rabbit;
import animal.Wolf;
import foliage.Grass;
import hole.Hole;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.Set;

public class Helper {

    public Helper() {

    }

    public Set<Location> getEmptySurroundingTiles(World world, Location location, int radius) {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location, radius);
        surroundingTiles.removeIf(tile -> !world.isTileEmpty(tile));
        return surroundingTiles;
    }

    public void setDisplayInfo(Program program) {

        //Set display for Grass
        DisplayInformation grassDisplay = new DisplayInformation(Color.black, "grass");
        program.setDisplayInformation(Grass.class, grassDisplay);

        //Set display for Rabbitholes
        DisplayInformation rabbitHoleDisplay = new DisplayInformation(Color.orange, "hole-small");
        program.setDisplayInformation(Hole.class, rabbitHoleDisplay);

        //Set display for Rabbit
        DisplayInformation rabbitDisplay = new DisplayInformation(Color.orange, "rabbit-large");
        program.setDisplayInformation(Rabbit.class, rabbitDisplay);

        //Set display for Wolf
        DisplayInformation wolfDisplay = new DisplayInformation(Color.orange, "wolf");
        program.setDisplayInformation(Wolf.class, wolfDisplay);

        //Set display for Bear
        DisplayInformation BearDisplay = new DisplayInformation(Color.orange, "bear");
        program.setDisplayInformation(Bear.class, BearDisplay);
    }
}
