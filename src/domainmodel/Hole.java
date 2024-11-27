package domainmodel;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;


public class Hole implements NonBlocking, DynamicDisplayInformationProvider {

    private final World ourWorld;
    private final Location tileLocation;
    private boolean hasAnimal;
    private HoleType type;

    Hole(World ourWorld, Location tileLocation, HoleType type) {
        this.ourWorld = ourWorld;
        this.tileLocation = tileLocation;
        this.type = type;
        Plane.increaseNonBlocking();

    }


    public void setHasAnimal(boolean hasAnimal) {
        this.hasAnimal = hasAnimal;
    }

    boolean getHasAnimal() {
        return hasAnimal;
    }

    public Location getTileLocation() {
        return tileLocation;
    }

    @Override
    public DisplayInformation getInformation(){
        switch (type){
            case RABBITHOLE:
                return new DisplayInformation(Color.black, "hole-small");

            case WOLFHOLE:
                return new DisplayInformation(Color.black, "hole");

        }

        return null;
    }

    public HoleType getHoleType() {
        return type;
    }
}
