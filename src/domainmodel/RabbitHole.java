package domainmodel;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.simulator.Actor;

import java.awt.*;


public class RabbitHole implements NonBlocking {

    private final World ourWorld;
    private final Location tileLocation;
    private boolean hasRabbit;
    private boolean isRabbitHole = false;

    public RabbitHole(World ourWorld, Location tileLocation) {
        this.ourWorld = ourWorld;
        this.tileLocation = tileLocation;
    }

    public void setHasRabbit(boolean hasRabbit) {
        this.hasRabbit = hasRabbit;
    }

    public boolean getHasRabbit() {
        return hasRabbit;
    }

    public Location getTileLocation() {
        return tileLocation;
    }

}
