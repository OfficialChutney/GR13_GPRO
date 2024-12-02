package animal;

import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Cadavar  implements Actor, NonBlocking {
    World world;
    int amountOfMeat;
    int stepsToDecompose;
    int currentSteps = 0;
    boolean mushrooms;

    public Cadavar(World world, boolean mushrooms, int amountOfMeat, int stepsToDecompose) {
        this.world = world;
        this.mushrooms = mushrooms;
        this.amountOfMeat = amountOfMeat;
        this.stepsToDecompose = stepsToDecompose;

    }

    @Override
    public void act(World world) {

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
        //...
    }
}
