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
    private final Random rd;
    private final int worldSizeSquared;

    public Grass(World ourWorld, Location tileLocation) {
        this.ourWorld = ourWorld;
        this.tileLocation = tileLocation;
        chanceToGrow = 0.5f;
        rd = new Random();
        worldSizeSquared = ourWorld.getSize() * ourWorld.getSize();
        Plane.increaseNonBlocking();

    }

    @Override
    public void act(World world) {
        spreadGrass();

    }

    private Location getTileLocation() {
        return tileLocation;
    }


    public void spreadGrass() {

        if(Plane.getNonBlocking() != worldSizeSquared) {
            if(!(rd.nextFloat(1) < chanceToGrow)) {
                return;
            }
        } else {
            return;
        }

        Set<Location> set = ourWorld.getSurroundingTiles();
        ArrayList<Location> list = new ArrayList<>(set);

        int i = 0;
        for (Location l : list) {
            if (!ourWorld.containsNonBlocking(l)) {
                break;
            }
            i++;
        }

        if (i != list.size()) {
            Location l = list.get(rd.nextInt(list.size()));

            while (ourWorld.containsNonBlocking(l)) {
                l = list.get(rd.nextInt(list.size()));
            }

            ourWorld.setTile(l, new Grass(ourWorld,l));
        }
    }

    public void deleteGrass() {
        ourWorld.delete(this);
        Plane.decreaseNonBlocking();
    }
}
