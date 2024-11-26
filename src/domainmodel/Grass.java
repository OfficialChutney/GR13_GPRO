package domainmodel;

import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.simulator.Actor;

import java.util.ArrayList;
import java.util.Set;
import java.util.Random;

public class Grass implements Actor, NonBlocking {

    private final Location tileLocation;
    private final World ourWorld;
    private final float chanceToGrow;

    public Grass(World ourWorld, Location tileLocation) {
        this.ourWorld = ourWorld;
        this.tileLocation = tileLocation;
        chanceToGrow = 0.1f;

    }

    @Override
    public void act(World world) {
        spreadGrass(ourWorld,chanceToGrow);
    }

    private Location getTileLocation() {
        return tileLocation;
    }

    private ArrayList<Location> getEmptyNeighbouringTiles() {
        Set<Location> neighbours = ourWorld.getSurroundingTiles();
        ArrayList<Location> list = new ArrayList<>(neighbours);

        ArrayList<Location> nonBlockingList = new ArrayList<>();
        for(Location location : list){
            if(!ourWorld.containsNonBlocking(location)){
                nonBlockingList.add(location);
            }
        }

        return nonBlockingList;
    }

    private void spreadGrass(World ourWorld, float chanceToGrow) {
        ArrayList<Location> emptyNeighbouringTiles = getEmptyNeighbouringTiles();
        Random chance = new Random();

        if(chance.nextFloat(1) < chanceToGrow && !emptyNeighbouringTiles.isEmpty()) { // success
            Random randLocation = new Random();
            Location l = emptyNeighbouringTiles.get(randLocation.nextInt(emptyNeighbouringTiles.size()));

            ourWorld.setTile(l, new Grass(ourWorld,l));
        }

    }

    public void deleteGrass(){
        ourWorld.delete(this);
    }

}
