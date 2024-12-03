package domainmodel;

import itumulator.world.Location;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InitialConditions {
    private String object;
    private String numberOfObjects;
    private Location coordinates;
    private boolean fungi;

    public InitialConditions(String object, String numberOfObjects, String fungi) {
        this.object = object;
        this.fungi = !(fungi == null);
        this.numberOfObjects = numberOfObjects;
        coordinates = null;
    }

    public InitialConditions(String object, String numberOfObjects, String fungi, String x, String y) {
        this.object = object;
        this.fungi = (fungi.equals("fungi"));
        this.numberOfObjects = numberOfObjects;
        coordinates = new Location(Integer.parseInt(x), Integer.parseInt(y));

    }


    public String getObject() {
        return object;
    }

    public String getNumberOfObjects() {
        return numberOfObjects;
    }

    public Location getCoordinates() {
        return coordinates;
    }

    public boolean isFungi() {
        return fungi;
    }
}
