package foliage;

import domainmodel.Plane;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.simulator.Actor;

import java.util.ArrayList;
import java.util.Set;
import java.util.Random;

/**
 * Græsset har til funktion at kunne sprede sig afhængigt af en defineret chance parameter. Græsset skal dertil også
 * kunne slettes.
 */
public class Grass implements Actor, NonBlocking {

    private final World ourWorld;
    private float chanceToGrow;
    private final Random rd;
    private final int worldSizeSquared;
    private boolean canSpread;

    public Grass(World ourWorld) {
        this.ourWorld = ourWorld;
        chanceToGrow = 0.5f;
        rd = new Random();
        worldSizeSquared = ourWorld.getSize() * ourWorld.getSize();
        canSpread = true;

    }

    @Override
    public void act(World world) {
        spreadGrass();

    }


    /**
     * spreadGrass tjekker først om den er inden for en chance parameter, og returnere hvis den ikke er.
     * Hvis metoden når videre fra denne chance tjek, tjekker den efterfølgende for ledige pladser rundt om sig selv.
     * Efterfølgende vælger den en tilfældig af disse, og placere en ny grls.
     */
    public void spreadGrass() {

        if(canSpread) {
            if(!(rd.nextFloat(1) < chanceToGrow)) {
                return;
            }
        } else {
            return;
        }

        Set<Location> set = ourWorld.getSurroundingTiles();
        ArrayList<Location> list = new ArrayList<>(set);

        int i = 0;
        for (Location l : list) {
            if (!ourWorld.containsNonBlocking(l)) {
                break;
            }
            i++;
        }

        if (i != list.size()) {
            Location l = list.get(rd.nextInt(list.size()));

            while (ourWorld.containsNonBlocking(l)) {
                l = list.get(rd.nextInt(list.size()));
            }

            ourWorld.setTile(l, new Grass(ourWorld));
        }
    }

    /**
     * Sletter sig selv
     */
    public void deleteGrass() {
        ourWorld.delete(this);
    }

    /**
     * Sætter en sandhedsværdi, afhængigt af, om græsset skal kunne sprede sig.
     * @param canSpread
     */
    public void setCanSpread(boolean canSpread) {
        this.canSpread = canSpread;
    }

    /**
     * Sætter attributten chanceToGrow til argumentet som indsættes.
     * @param chanceToGrow
     */
    public void setChanceToGrow(float chanceToGrow) {
        this.chanceToGrow = chanceToGrow;
    }
}
