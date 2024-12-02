package domainmodel;

import itumulator.world.Location;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InitialConditions {
    private String object;
    private String numberOfObjects;
    private Location coordinates;

    public InitialConditions(String object, String numberOfObjects) {
        this.object = object;
        this.numberOfObjects = numberOfObjects;
        coordinates = null;
    }

    public InitialConditions(String object, String numberOfObjects, String coordinates) {
        this.object = object;
        this.numberOfObjects = numberOfObjects;


        Pattern pattern = Pattern.compile("\\((\\d+),(\\d+)\\)");

        Matcher matcher = pattern.matcher(coordinates);

        if (matcher.matches()) {

            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));

            this.coordinates = new Location(x, y);
        } else {
            throw new NumberFormatException("Match could not be found for coordinates: " + coordinates);
        }

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


}
