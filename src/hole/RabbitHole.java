package hole;

import animal.Rabbit;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;

public class RabbitHole extends Hole {

    private ArrayList<Rabbit> rabbitsInHole;

    public RabbitHole(World ourWorld, Location tileLocation) {
        super(ourWorld, tileLocation);
        rabbitsInHole = new ArrayList<>();
    }

    public void addRabbit(Rabbit rabbit) {
        rabbitsInHole.add(rabbit);
    }

    public void removeRabbit(Rabbit rabbit) {
        rabbitsInHole.remove(rabbit);
    }

    public ArrayList<Rabbit> getRabbitsInHole() {
        return rabbitsInHole;
    }
}
