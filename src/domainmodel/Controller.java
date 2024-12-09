package domainmodel;

import java.util.HashMap;
import java.util.LinkedList;

public class Controller {


    public Controller() {
    }

    public TestPackage initiateSimulation(int worldSize, boolean isTest, LinkedList<InitialConditions> icList) {
        Plane plane = new Plane();
        return plane.startSimulation(worldSize, isTest, icList);
    }

}
