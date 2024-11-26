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

    void createWolfList(){
        wolfs.add(new Wolf(true, wolfPackID, this));

        for(int i=1;i<numberOfWolfs;i++){
            wolfs.add(new Wolf(false, wolfPackID, this));
        }
    }

    void spawnWolfPack(Location spawnLocation, World world){
        world.setTile(spawnLocation, wolfs.get(0));

        Set set = world.getEmptySurroundingTiles(spawnLocation);

        ArrayList<Location> spawnLocations = new ArrayList<>(set);

        for(int i=1;i<numberOfWolfs;i++){
            world.setTile(spawnLocations.get(i), wolfs.get(i));
        }
    }

}
