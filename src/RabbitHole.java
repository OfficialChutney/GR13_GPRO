import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.simulator.Actor;

import java.util.ArrayList;
import java.util.Set;


public class RabbitHole implements NonBlocking, Actor {

    private final World ourWorld;
    private final Location tileLocation;
    private boolean hasRabbit;

    RabbitHole(World ourWorld, Location tileLocation) {
        this.ourWorld = ourWorld;
        this.tileLocation = tileLocation;
    }

    @Override
    public void act(World ourWorld) {
        //hej

    }

    public void setHasRabbit(boolean hasRabbit) {
        this.hasRabbit = hasRabbit;
    }

    boolean getHasRabbit() {
        return hasRabbit;
    }

    public Location getTileLocation() {
        return tileLocation;
    }


}
