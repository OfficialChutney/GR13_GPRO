package hole;

import itumulator.executable.DisplayInformation;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;

/**
 * Ulve hullet nedarver fra den abstrakte klasse Hole. Klassen fungere som et hul for ulve.
 */
public class WolfHole extends Hole {

    public int ownerPackID;

    public WolfHole(World ourWorld, Location tileLocation, int ownerPackID) {
        super(ourWorld, tileLocation);
        this.ownerPackID = ownerPackID;
    }



}
