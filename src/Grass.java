import itumulator.world.Location;
import itumulator.world.World;
import itumulator.simulator.Actor;

import java.util.ArrayList;
import java.util.Set;
import java.util.Random;

public class Grass implements Actor {

    Location tileLocation;
    World ourWorld;
    float chanceToGrow;

    Grass(World ourWorld, Location tileLocation, float chanceToGrow) {
        this.ourWorld = ourWorld;
        this.tileLocation = tileLocation;
        this.chanceToGrow = chanceToGrow;
    }

    @Override
    public void act(World world) {
        spreadGrass(ourWorld,tileLocation,chanceToGrow);
    }

    Location getTileLocation() {
        return tileLocation;
    }

    ArrayList<Location> getEmptyNeighbouringTiles() {
        Set<Location> neighbours = ourWorld.getEmptySurroundingTiles();
        ArrayList<Location> list = new ArrayList<>(neighbours);
        return list;
    }

    void spreadGrass(World ourWorld, Location tileLocation, float chanceToGrow) {
        ArrayList<Location> emptyNeighbouringTiles = getEmptyNeighbouringTiles();
        Random chance = new Random();

        if(chance.nextFloat(1) < chanceToGrow && !emptyNeighbouringTiles.isEmpty()) { // success
            Random randLocation = new Random();
            Location l = emptyNeighbouringTiles.get(randLocation.nextInt(emptyNeighbouringTiles.size()));

            ourWorld.setTile(l, new Grass(ourWorld,l,chanceToGrow));
        }

    }

    void deleteGrass(){
        ourWorld.delete(this);
    }

}
