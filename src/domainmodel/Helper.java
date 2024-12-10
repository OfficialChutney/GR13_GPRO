package domainmodel;

import animal.carnivores.Bear;
import animal.Cadavar;
import animal.herbivore.Rabbit;
import animal.carnivores.Wolf;
import foliage.Grass;
import hole.RabbitHole;
import hole.WolfHole;
import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.simulator.Simulator;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.Set;

/**
 * Klasse indeholdene statiske metoder, der kan tilg�s af alle klasser. Dens prim�re funktion er at s�tte {@link DisplayInformation} for
 * de objekter, der ikke implementere {@link itumulator.executable.DynamicDisplayInformationProvider}. Dog tager den ogs� imod {@link Simulator} fra {@link Plane},
 * Som benyttes af objekter til at bestemme, hvor mange steps verden er p�.
 * */
public class Helper {

    private static Simulator sim;


    /**
     * Metode kopieret fra {@link World#getEmptySurroundingTiles(Location)}. Dog redigeret, s� den kan tage imod en radius, og returnere
     * alle tomme tiles i en radius fra den {@link Location} der inds�ttes i startparametrene.
     * @param world det {@link World} der skal tjekkes.
     * @param location Den {@link Location}, hvor der tjekkes om der er tomme tiles rundt om.
     * @param radius er den radius, som man �nsker at tjekke rundt om.
     * @return Returnere et {@link Set} af {@link Location} for de tomme felter, der er rundt om {@param location} i den givne {@param radius}.
     */
    public static Set<Location> getEmptySurroundingTiles(World world, Location location, int radius) {
        Set<Location> surroundingTiles = world.getSurroundingTiles(location, radius);
        surroundingTiles.removeIf(tile -> !world.isTileEmpty(tile));
        return surroundingTiles;
    }

    /**
     * S�tter {@link DisplayInformation} for alle de objekter, der ikke implementere {@link itumulator.executable.DynamicDisplayInformationProvider}.
     * @param program det program, som {@link DisplayInformation} skal s�ttes for.
     */
    public static void setDisplayInfo(Program program) {

        //Set display for Grass
        DisplayInformation grassDisplay = new DisplayInformation(Color.black, "grass");
        program.setDisplayInformation(Grass.class, grassDisplay);

        //Set display for Rabbitholes
        DisplayInformation rabbitHoleDisplay = new DisplayInformation(Color.orange, "hole-small");
        program.setDisplayInformation(RabbitHole.class, rabbitHoleDisplay);

        //Set display for Rabbit
        DisplayInformation rabbitDisplay = new DisplayInformation(Color.orange, "rabbit-large");
        program.setDisplayInformation(Rabbit.class, rabbitDisplay);

        //Set display for Wolf
        DisplayInformation wolfDisplay = new DisplayInformation(Color.orange, "wolf");
        program.setDisplayInformation(Wolf.class, wolfDisplay);

        //Set display for Bear
        DisplayInformation BearDisplay = new DisplayInformation(Color.orange, "bear");
        program.setDisplayInformation(Bear.class, BearDisplay);

        //Set display for WolfHole
        DisplayInformation WolfHole = new DisplayInformation(Color.orange, "hole");
        program.setDisplayInformation(WolfHole.class, WolfHole);

        //Set display for Cadaver
        DisplayInformation Cadaver = new DisplayInformation(Color.orange, "carcass");
        program.setDisplayInformation(Cadavar.class, Cadaver);

    }

    /**
     * S�tter Simulator attributten i denne klasse.
     * @param sim tager imod den {@link Simulator} som skal s�ttes.
     */
    public static void setSimulator(Simulator sim) {
        Helper.sim = sim;
    }

    /**
     * Returnere den p�g�ldende verdens steps.
     * @return int som er antallet af steps i verdenen
     */
    public static int getSteps() {
        return sim.getSteps();
    }


}
