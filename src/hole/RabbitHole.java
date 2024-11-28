package hole;

import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;


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
