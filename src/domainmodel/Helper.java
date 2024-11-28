package domainmodel;

import itumulator.world.Location;
import itumulator.world.World;

import java.util.Set;

public class Helper {


    private World world;
    public Helper(World world) {
        this.world = world;
    }

    public Set<Location> getEmptySurroundingTiles(Location location, int radius) {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location, radius);
        surroundingTiles.removeIf(tile -> !world.isTileEmpty(tile));
        return surroundingTiles;
    }
}
