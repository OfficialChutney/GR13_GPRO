package foliage;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;

/**
 * BerryBush er klassen som styre adfæret af buskene i simulationen.
 */
public class BerryBush implements Actor, NonBlocking, DynamicDisplayInformationProvider {
    private Location tileLocation;
    private boolean hasBerries = true;

    public BerryBush() {
        this.tileLocation = this.getTileLocation();
    }

    /**
     * act tjekker om busken har bær, hvis den ikke har kalder den chanceToGrowBerries.
     * @param world providing details of the position on which the actor is currently located and much more.
     */
    @Override
    public void act(World world) {
        if (!hasBerries) {
            chanceToGrowBerries();
        }
    }

    /**
     * chanceToGrowBerries har en chance for at sætte hasBerries til True.
     */
    private void chanceToGrowBerries() {
        Random rand = new Random();
        if (!hasBerries && rand.nextInt(8) > 5) {
            hasBerries = true;
        }
    }

    /**
     * retunere buskens Location.
     * @return Location
     */
    public Location getTileLocation() {
        return tileLocation;
    }

    /**
     * eatBerries sætter hasBerries til False.
     */
    public void eatBerries(){
        hasBerries = false;
    }

    /**
     * BerryState retunere hasBerries
     * @return Boolean
     */
    public boolean BerryState() {
        return hasBerries;
    }

    /**
     * getInformation retunere den nødvendige DisplayInformation ud fra busken status.
     * @return DisplayInformation
     */
    @Override
    public DisplayInformation getInformation() {
        if(hasBerries) {
            return new DisplayInformation(Color.red, "bush-berries");
        } else {
            return new DisplayInformation(Color.green, "bush");
        }
    }
}
