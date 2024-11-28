package hole;

import domainmodel.Plane;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;


public abstract class Hole implements NonBlocking {

    private final World ourWorld;
    private final Location tileLocation;
    private boolean hasAnimal;

    public Hole(World ourWorld, Location tileLocation) {
        this.ourWorld = ourWorld;
        this.tileLocation = tileLocation;
        Plane.increaseNonBlocking();
    }

    public void setHasAnimal(boolean hasAnimal) {
        this.hasAnimal = hasAnimal;
    }

    public boolean getHasAnimal() {
        return hasAnimal;
    }

    public Location getTileLocation() {
        return tileLocation;
    }
}
