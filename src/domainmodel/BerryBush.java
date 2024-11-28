package domainmodel;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.Random;

public class BerryBush implements Actor, NonBlocking {
    private Location tileLocation;
    private boolean hasBerries = true;

    public BerryBush() {
        this.tileLocation = this.getTileLocation();
    }

    @Override
    public void act(World world) {
        if (!hasBerries) {
            chanceToGrowBerries();
        }
    }

    private void chanceToGrowBerries() {
        Random rand = new Random();
        if (!hasBerries && rand.nextInt(8) > 5) {
            hasBerries = true;
        }
    }
    public Location getTileLocation() {
        return tileLocation;
    }

    public void eatBerries(){
        hasBerries = false;
    }

    public boolean BerryState() {
        return hasBerries;
    }
}
