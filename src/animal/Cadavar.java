package animal;

import foliage.Mushroom;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

/**
 * cadavar er en mad kilde til de dyr som spiser kød, samtidigt giver de også chance for at svampe spawner.
 */
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

    /**
     * act bruges til at tælde mod cadaveres decomposeOrDelete.
     * @param world providing details of the position on which the actor is currently located and much more.
     */
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

    /**
     * decomposeOrDelete sletter dette Cadavar hvis steps matcher stepsToDecompose eller amountOfMeat er 0 eller under.
     */
    private void decomposeOrDelete(){
        if(currentSteps >= stepsToDecompose || amountOfMeat <= 0){
            world.delete(this);

            if(mushrooms){
                setMushroomInWorld();
            }
        }
    }

    /**
     * reduceAmountOfMeat reducere amountOfMeat med det givet parameter.
     * @param amountOfMeat
     */
    public void reduceAmountOfMeat(int amountOfMeat){
        this.amountOfMeat -= amountOfMeat;
    }

    /**
     * setMushroomInWorld sætter en Mushroom i World på den Location som dette Cadavar er på.
     */
    private void setMushroomInWorld(){
        Mushroom mushroom = new Mushroom(world);
        world.setTile(myLocation, mushroom);
    }

    /**
     * setMushroomState sætter mushrooms til enden true eller false.
     * @param mushrooms
     */
    public void setMushroomState(boolean mushrooms){
        this.mushrooms = mushrooms;
    }
}
