package domainmodel;

import java.util.ArrayList;
import java.util.Set;

import itumulator.world.Location;
import itumulator.world.World;

public class WolfPack {

    int numberOfWolfs;
    ArrayList<Wolf> wolfs;
    int wolfPackID;

    WolfPack(int numberOfWolfs, Location spawnLocation, World world) {

        this.numberOfWolfs = numberOfWolfs;
        this.wolfPackID = hashCode();

        createWolfList();

        spawnWolfPack(spawnLocation, world);
    }

    void createWolfList() {
        wolfs.add(new Wolf(true, wolfPackID, this));

        for (int i = 1; i < numberOfWolfs; i++) {
            wolfs.add(new Wolf(false, wolfPackID, this));
        }
    }

    void spawnWolfPack(Location spawnLocation, World world) {
        Wolf leaderWolf = wolfs.getFirst();
        leaderWolf.setMyLocation(spawnLocation);

        world.setTile(spawnLocation, leaderWolf);

        spawnWolfsInWolfPack(numberOfWolfs, spawnLocation, world, 1);

    }

    Wolf getWolfLeader() {
        return wolfs.getFirst();
    }

    public Set<Location> getEmptySurroundingTiles(World world, Location location, int radius) {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location, radius);
        surroundingTiles.removeIf(tile -> !world.isTileEmpty(tile));
        return surroundingTiles;
    }

    private void spawnWolfsInWolfPack(int numberOfWolfs, Location spawnLocation, World world, int radius) {
        Set set = getEmptySurroundingTiles(world, spawnLocation,radius);

        ArrayList<Location> spawnLocations = new ArrayList<>(set);

        for (int i = 1; i < spawnLocations.size(); i++) {
            if(i > numberOfWolfs) {
                break;
            }
            world.setTile(spawnLocations.get(i-1), wolfs.get(i));
        }

        if(numberOfWolfs > spawnLocations.size()) {
            spawnWolfsInWolfPack(numberOfWolfs - spawnLocations.size(), spawnLocation, world, (radius+1));
        }
    }

}
