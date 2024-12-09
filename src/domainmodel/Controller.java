package domainmodel;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Controlleren instansiere {@link Plane} og starter en simulation.
 */
public class Controller {


    public Controller() {
    }

    /**
     * Starter en simulation af en verden. Metoden instansiere {@link Plane}.
     * @param worldSize størrelsen på verdenen.
     * @param isTest hvorvidt denne simulation er en del af en UnitTest
     * @param icList listen af startparametre.
     * @return {@link TestPackage} som indeholder slutParametre for verden.
     */
    public TestPackage initiateSimulation(int worldSize, boolean isTest, LinkedList<InitialConditions> icList) {
        Plane plane = new Plane();
        return plane.startSimulation(worldSize, isTest, icList);
    }

}
