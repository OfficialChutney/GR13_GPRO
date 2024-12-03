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

    public Hole(World ourWorld, Location tileLocation) {
        this.ourWorld = ourWorld;
        this.tileLocation = tileLocation;
    }

    public Location getTileLocation() {
        return tileLocation;
    }
}
