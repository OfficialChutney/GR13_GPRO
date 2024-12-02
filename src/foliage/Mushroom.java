package foliage;

import animal.Cadavar;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.Set;

public class Mushroom implements Actor, NonBlocking {
    World world;
    int steps = 0;
    int stepsUntilDecompose = 80;
    boolean didItSpread;

    public Mushroom(World world) {
        this.world = world;
    }

    @Override
    public void act(World world) {
           boolean didItSpread = false;

           spread();

           if(didItSpread){
               resetCountDown();
           } else {
               steps++;
           }

           if(steps >= stepsUntilDecompose){
               world.delete(this);
           }
    }

    private void spread() {
        Set<Location> neighbours = world.getSurroundingTiles(world.getLocation(this));
        ArrayList<Location> surrondingTilesList = (ArrayList) neighbours;

        for (int i = 0; i < surrondingTilesList.size(); i++) {
            Object object = world.getTile(surrondingTilesList.get(i));

            if(object instanceof Cadavar cadavar){
                cadavar.setMushroom(true);
            }
        }

        didItSpread = true;
    }

    private void resetCountDown() {
        steps = 0;
    }
}
