package domainmodel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import itumulator.world.Location;
import itumulator.world.World;

public class WolfPack {

    int numberOfWolfs;
    ArrayList<Wolf> wolfs;
    int wolfPackID;

    WolfPack(int numberOfWolfs, Location spawnLocation, World world) {
        wolfs = new ArrayList<>();
        this.numberOfWolfs = numberOfWolfs;
        this.wolfPackID = hashCode();

        createWolfList();

        spawnWolfPack(spawnLocation, world);
    }

    void createWolfList() {
        wolfs.add(new Wolf(wolfPackID, this));

        for (int i = 1; i < numberOfWolfs; i++) {
            wolfs.add(new Wolf(wolfPackID, this, wolfs.getFirst()));
        }
    }

    void spawnWolfPack(Location spawnLocation, World world) {
        Wolf leaderWolf = wolfs.getFirst();
        leaderWolf.setMyLocation(spawnLocation);

        world.setTile(spawnLocation, leaderWolf);

        ArrayList<Wolf> tempWolfs = new ArrayList<>(wolfs);
        tempWolfs.remove(tempWolfs.getFirst());

        spawnWolfsInWolfPack(numberOfWolfs, spawnLocation, world, 1, tempWolfs);

    }

    Wolf getWolfLeader() {
        return wolfs.getFirst();
    }

    public Set<Location> getEmptySurroundingTiles(World world, Location location, int radius) {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location, radius);
        surroundingTiles.removeIf(tile -> !world.isTileEmpty(tile));
        return surroundingTiles;
    }

    private void spawnWolfsInWolfPack(int numberOfWolfs, Location spawnLocation, World world, int radius, ArrayList<Wolf> wolfs) {
        Set<Location> set = getEmptySurroundingTiles(world, spawnLocation,radius);

        ArrayList<Location> spawnLocations = new ArrayList<>(set);
        ArrayList<Wolf> tempWolfs = new ArrayList<>(wolfs);
        for (int i = 0; i < spawnLocations.size(); i++) {
            if(i > numberOfWolfs-2) {
                break;
            }
            wolfs.get(i).setMyLocation(spawnLocations.get(i));
            world.setTile(spawnLocations.get(i), wolfs.get(i));
            tempWolfs.remove(wolfs.get(i));
        }

        if(!tempWolfs.isEmpty()) {
            spawnWolfsInWolfPack(numberOfWolfs - spawnLocations.size(), spawnLocation, world, (radius+1), tempWolfs);
        }
    }

}
