package foliage;

import domainmodel.Plane;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.simulator.Actor;

import java.util.ArrayList;
import java.util.Set;
import java.util.Random;

public class Grass implements Actor, NonBlocking {

    private final World ourWorld;
    private final float chanceToGrow;
    private final Random rd;
    private final int worldSizeSquared;
    private boolean canSpread;

    public Grass(World ourWorld) {
        this.ourWorld = ourWorld;
        chanceToGrow = 0.5f;
        rd = new Random();
        worldSizeSquared = ourWorld.getSize() * ourWorld.getSize();
        canSpread = true;

    }

    @Override
    public void act(World world) {
        spreadGrass();

    }


    public void spreadGrass() {

        if(canSpread) {
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

            ourWorld.setTile(l, new Grass(ourWorld));
        }
    }

    public void deleteGrass() {
        ourWorld.delete(this);
    }

    public void setCanSpread(boolean canSpread) {
        this.canSpread = canSpread;
    }
}
