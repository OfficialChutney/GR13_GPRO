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
        System.out.println("Object: "+object);
        System.out.println("Fungi: "+ fungi);
        System.out.println("Number of objects: "+numberOfObjects);
        System.out.println("Coordinates: "+coordinates);
    }

    public InitialConditions(String object, String numberOfObjects, String fungi, String x, String y) {
        this.object = object;
        this.fungi = !(fungi == null);
        this.numberOfObjects = numberOfObjects;
        coordinates = new Location(Integer.parseInt(x), Integer.parseInt(y));
        System.out.println("Object: "+object);
        System.out.println("Fungi: "+ fungi);
        System.out.println("Number of objects: "+numberOfObjects);
        System.out.println("Coordinates: "+coordinates);

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
