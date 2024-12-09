package hole;

import animal.Rabbit;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;

/**
 * Klassen nedarver fra den abstrakte klasse Hole. Klassen har til funktion, at kende sin placering, og have en liste over,
 * hvilke og hvor mange kaniner er i hullet.
 */
public class RabbitHole extends Hole {

    private ArrayList<Rabbit> rabbitsInHole;

    public RabbitHole(World ourWorld, Location tileLocation) {
        super(ourWorld, tileLocation);
        rabbitsInHole = new ArrayList<>();
    }

    /**
     * Tilføjer argumentet Rabbit til listen af rabbitsInHole
     * @param rabbit
     */
    public void addRabbit(Rabbit rabbit) {
        rabbitsInHole.add(rabbit);
    }

    /**
     * Fjerner argumentet Rabbit fra listen af rabbitsInHole
     * @param rabbit
     */
    public void removeRabbit(Rabbit rabbit) {
        rabbitsInHole.remove(rabbit);
    }

    /**
     * Returnere listen af rabbitsInHole
     * @return rabbitsInHole
     */
    public ArrayList<Rabbit> getRabbitsInHole() {
        return rabbitsInHole;
    }
}
