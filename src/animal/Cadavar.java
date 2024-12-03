package animal;

import foliage.Mushroom;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Cadavar implements Actor, NonBlocking {
    World world;
    int amountOfMeat;
    int stepsToDecompose;
    int currentSteps = 0;
    boolean mushrooms;
    Location myLocation;

    public Cadavar(World world, boolean mushrooms, int amountOfMeat, int stepsToDecompose) {
        this.world = world;
        this.mushrooms = mushrooms;
        this.amountOfMeat = amountOfMeat;
        this.stepsToDecompose = stepsToDecompose;

    }

    @Override
    public void act(World world) {
        if(myLocation == null) {
            myLocation = world.getLocation(this);
        }
        decomposeOrDelete();
        currentSteps++;
        if(mushrooms) {
            currentSteps++;
        }
    }

    private void decomposeOrDelete(){
        if(currentSteps >= stepsToDecompose || amountOfMeat <= 0){
            world.delete(this);

            if(mushrooms){
                setMushroomInWorld();
            }
        }
    }

    public void reduceAmountOfMeat(int amountOfMeat){
        this.amountOfMeat -= amountOfMeat;
    }

    private void setMushroomInWorld(){
        Mushroom mushroom = new Mushroom(world);
        world.setTile(myLocation, mushroom);
    }

    public void setMushroomState(boolean mushrooms){
        this.mushrooms = mushrooms;
    }
}
