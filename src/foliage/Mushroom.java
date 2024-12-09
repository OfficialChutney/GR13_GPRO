package foliage;

import animal.Cadavar;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class Mushroom implements Actor, NonBlocking, DynamicDisplayInformationProvider {
    World world;
    int steps = 0;
    int age = 0;
    int stepsUntilDecompose;

    public Mushroom(World world, int stepsUntilDecompose) {
        this.world = world;
        this.stepsUntilDecompose = stepsUntilDecompose;
    }

    @Override
    public void act(World world) {

           spread();

           age++;
           steps++;

           if(steps >= stepsUntilDecompose){
               world.delete(this);
           }
    }

    private void spread() {
        Set<Location> neighbours = world.getSurroundingTiles(world.getLocation(this));
        ArrayList<Location> surrondingTilesList = new ArrayList<>(neighbours);

        for (int i = 0; i < surrondingTilesList.size(); i++) {
            Object object = world.getTile(surrondingTilesList.get(i));

            if(object instanceof Cadavar cadavar){
                cadavar.setMushroomState(true);
                resetCountDown();
            }
        }

    }

    private void resetCountDown() {
        steps = 0;
    }

    @Override
    public DisplayInformation getInformation() {
        if(age > 20) {
            return new DisplayInformation(Color.WHITE, "fungi");
        } else {
            return new DisplayInformation(Color.WHITE, "fungi-small");

        }
    }
}
