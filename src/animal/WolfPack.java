package animal;

import java.util.ArrayList;
import java.util.Set;

import domainmodel.Helper;
import itumulator.world.Location;
import itumulator.world.World;

public class WolfPack {

    private int numberOfWolves;
    private ArrayList<Wolf> wolves;
    private int wolfPackID;
    private World world;

    public WolfPack(int numberOfWolves, Location spawnLocation, World world) {
        wolves = new ArrayList<>();
        this.numberOfWolves = numberOfWolves;
        this.wolfPackID = hashCode();
        this.world = world;

        createWolfList();

        spawnWolfPack(spawnLocation);
    }

    private void createWolfList() {
        wolves.add(new Wolf(world, wolfPackID, this));

        for (int i = 1; i < numberOfWolves; i++) {
            wolves.add(new Wolf(world, wolfPackID, this, wolves.getFirst()));
        }
    }

    private void spawnWolfPack(Location spawnLocation) {
        Wolf leaderWolf = wolves.getFirst();
        leaderWolf.setMyLocation(spawnLocation);

        world.setTile(spawnLocation, leaderWolf);

        ArrayList<Wolf> tempWolves = new ArrayList<>(wolves);
        tempWolves.remove(tempWolves.getFirst());

        spawnWolfsInWolfPack(numberOfWolves, spawnLocation, 1, tempWolves);

    }

    Wolf getWolfLeader() {
        return wolves.getFirst();
    }


    private void spawnWolfsInWolfPack(int numberOfWolfs, Location spawnLocation, int radius, ArrayList<Wolf> wolfs) {
        Set<Location> set = Helper.getEmptySurroundingTiles(world, spawnLocation,radius);

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
            spawnWolfsInWolfPack(numberOfWolfs - spawnLocations.size(), spawnLocation, (radius+1), tempWolfs);
        }
    }

}
