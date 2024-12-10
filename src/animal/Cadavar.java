package animal;

import foliage.Mushroom;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

/**
 * Cadavar er en mad kilde til de dyr som spiser kød, samtidigt giver de også chance for at {@link Mushroom} spawner.
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
     * act bruges til at tælle mod cadaveres decomposeOrDelete.
     * @param world den verden som objektet befinder sig i.
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
     * Sletter dette Cadavar hvis steps matcher {@link #stepsToDecompose} eller {@link #amountOfMeat} er 0 eller under.
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
     * Reducere {@link #amountOfMeat} med det givet parameter.
     * @param amountOfMeat mængden som {@link #amountOfMeat} skal reduceres med.
     */
    public void reduceAmountOfMeat(int amountOfMeat){
        this.amountOfMeat -= amountOfMeat;
    }

    /**
     * Sætter en {@link Mushroom} i World på den Location som dette {@link Cadavar} er på.
     */
    private void setMushroomInWorld(){
        Mushroom mushroom = new Mushroom(world, amountOfMeat);
        world.setTile(myLocation, mushroom);
    }

    /**
     * Sætter mushrooms til enten true eller false.
     * @param mushrooms boolean som repræsentere, om der er svamp i dette cadaver.
     */
    public void setMushroomState(boolean mushrooms){
        this.mushrooms = mushrooms;
    }

    /**
     * Returnere hvorvidt der er svamp i dette cadavar.
     * @return boolean som repræsentere hvorvidt der er svamp i dette cadavar.
     */
    public boolean isMushrooms() {
        return mushrooms;
    }
}
