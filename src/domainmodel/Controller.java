package domainmodel;

import java.util.HashMap;
import java.util.LinkedList;

public class Controller {

    private Plane plane;

    public Controller() {
        plane = new Plane();
    }

    public TestPackage initiateSimulation(int worldSize, boolean isTest, LinkedList<InitialConditions> icList) {

        return plane.startSimulation(worldSize, isTest, icList);
    }

    public Plane getPlane() {
        return plane;
    }

}
