import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.simulator.Actor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;


public class RabbitHole implements NonBlocking, Actor {

    private World ourWorld;
    private final Location mainLocation;
    private HashSet<RabbitHole> entrances;
    private boolean hasRabbit;
    private float chanceOfEntrance = 0.1f;

    RabbitHole(World ourWorld, Location tileLocation) {
        this.ourWorld = ourWorld;
        this.mainLocation = tileLocation;
        entrances = new HashSet<>();
        createEntrances();
    }

    @Override
    public void act(World ourWorld) {
        //hej

    }

    public void setHasRabbit(boolean hasRabbit) {
        this.hasRabbit = hasRabbit;
    }

    boolean getHasRabbit() {
        return hasRabbit;
    }

    public Location getMainLocation() {
        return mainLocation;
    }

    private void createEntrances() {
        Random rd = new Random();
        if (rd.nextFloat(1) < chanceOfEntrance) {
            Map<Object, Location> allEntities = ourWorld.getEntities();
            ArrayList<RabbitHole> rabbitHoles = new ArrayList<>();
            for (Object currentObject : allEntities.keySet()) {
                if (currentObject instanceof RabbitHole x && currentObject != this) {
                    rabbitHoles.add(x);
                }
            }

            if (!rabbitHoles.isEmpty()) {


                int indexToConnect = rd.nextInt(rabbitHoles.size());
                RabbitHole holeToConnect = rabbitHoles.get(indexToConnect);
                holeToConnect.addToEntrance(this);
                entrances.add(holeToConnect);
                rabbitHoles.remove(holeToConnect);

                for (RabbitHole otherHoles : rabbitHoles) {
                    if (rd.nextInt(5) == 1) {
                        entrances.add(otherHoles);
                        otherHoles.addToEntrance(this);
                    }
                }

            }
        }

        System.out.println(entrances.toString());
    }

    public void addToEntrance(RabbitHole rabbitHole) {
        entrances.add(rabbitHole);
        System.out.println(entrances.toString());
    }


}
