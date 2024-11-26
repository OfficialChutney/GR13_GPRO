package domainmodel;

import java.util.HashMap;

public class Controller {

    private Plane plane;

    public void initiateSimulation(int worldSize, HashMap<String, String> initialConditions) {
        plane = new Plane();
        plane.startSimulation(worldSize, initialConditions);
    }

}
