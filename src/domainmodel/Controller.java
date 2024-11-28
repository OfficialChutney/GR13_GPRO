package domainmodel;

import java.util.HashMap;

public class Controller {

    private Plane plane;

    public Controller() {
        plane = new Plane();
    }

    public TestPackage initiateSimulation(int worldSize, HashMap<String, String> initialConditions, boolean isTest) {

        return plane.startSimulation(worldSize, initialConditions, isTest);
    }

    public Plane getPlane() {
        return plane;
    }

}
