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
 * Gr�sset har til funktion at kunne sprede sig afh�ngigt af en defineret chance parameter. Gr�sset skal dertil ogs�
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
        chanceToGrow = 0.2f;
        rd = new Random();
        worldSizeSquared = ourWorld.getSize() * ourWorld.getSize();
        canSpread = true;

    }

    @Override
    public void act(World world) {
        spreadGrass();

    }


    /**
     * spreadGrass tjekker f�rst om den er inden for en chance parameter, og returnere hvis den ikke er.
     * Hvis metoden n�r videre fra denne chance tjek, tjekker den efterf�lgende for ledige pladser rundt om sig selv.
     * Efterf�lgende v�lger den en tilf�ldig af disse, og placere en ny grls.
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
     * S�tter en sandhedsv�rdi, afh�ngigt af, om gr�sset skal kunne sprede sig.
     * @param canSpread
     */
    public void setCanSpread(boolean canSpread) {
        this.canSpread = canSpread;
    }

    /**
     * S�tter attributten chanceToGrow til argumentet som inds�ttes.
     * @param chanceToGrow
     */
    public void setChanceToGrow(float chanceToGrow) {
        this.chanceToGrow = chanceToGrow;
    }
}
