package foliage;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;

public class BerryBush implements Actor, NonBlocking, DynamicDisplayInformationProvider {
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

    @Override
    public DisplayInformation getInformation() {
        if(hasBerries) {
            return new DisplayInformation(Color.red, "bush-berries");
        } else {
            return new DisplayInformation(Color.green, "bush");
        }
    }
}
